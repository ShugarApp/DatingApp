package com.dating.home.data.message

import com.dating.home.data.dto.websocket.OutgoingWebSocketDto
import com.dating.home.data.dto.websocket.WebSocketMessageDto
import com.dating.home.data.mappers.toDomain
import com.dating.home.data.mappers.toEntity
import com.dating.home.data.mappers.toWebSocketDto
import com.dating.home.data.network.KtorWebSocketConnector
import com.dating.home.database.AppChatDatabase
import com.dating.home.database.entities.ChatMessageEntity
import com.dating.home.domain.message.ChatMediaService
import com.dating.home.domain.message.ChatMessageService
import com.dating.home.domain.message.MessageRepository
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.MessageType
import com.dating.home.domain.models.MessageWithSender
import com.dating.home.domain.models.OutgoingNewMessage
import com.dating.core.data.database.safeDatabaseUpdate
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: AppChatDatabase,
    private val chatMessageService: ChatMessageService,
    private val chatMediaService: ChatMediaService,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val webSocketConnector: KtorWebSocketConnector,
    private val applicationScope: CoroutineScope
): MessageRepository {

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val dto = message.toWebSocketDto()

            val localUser = sessionStorage.observeAuthInfo().first()?.user
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            val entity = dto.toEntity(
                senderId = localUser.id,
                deliveryStatus = ChatMessageDeliveryStatus.SENDING
            )
            database.chatMessageDao.upsertMessage(entity)

            return webSocketConnector
                .sendMessage(dto.toJsonPayload())
                .onFailure { error ->
                    applicationScope.launch {
                        database.chatMessageDao.updateDeliveryStatus(
                            messageId = entity.messageId,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
                            status = ChatMessageDeliveryStatus.FAILED.name
                        )
                    }.join()
                }
        }
    }

    override suspend fun sendMediaMessage(
        chatId: String,
        messageId: String,
        mediaBytes: ByteArray,
        mimeType: String,
        messageType: MessageType
    ): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val localUser = sessionStorage.observeAuthInfo().first()?.user
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            // Create optimistic local message with placeholder content
            val entity = ChatMessageEntity(
                messageId = messageId,
                chatId = chatId,
                senderId = localUser.id,
                content = "",
                timestamp = Clock.System.now().toEpochMilliseconds(),
                deliveryStatus = ChatMessageDeliveryStatus.SENDING.name,
                messageType = messageType.name
            )
            database.chatMessageDao.upsertMessage(entity)

            // Step 1: Get upload URL
            val uploadInfo = when (val result = chatMediaService.getUploadUrl(chatId, mimeType)) {
                is Result.Success -> result.data
                is Result.Failure -> {
                    markAsFailed(messageId)
                    return Result.Failure(result.error)
                }
            }

            // Step 2: Upload to Supabase
            when (val result = chatMediaService.uploadMedia(uploadInfo.uploadUrl, uploadInfo.headers, mediaBytes)) {
                is Result.Success -> Unit
                is Result.Failure -> {
                    markAsFailed(messageId)
                    return Result.Failure(result.error)
                }
            }

            // Step 3: Send message via WebSocket with public URL as content
            val dto = OutgoingWebSocketDto.NewMessage(
                chatId = chatId,
                messageId = messageId,
                content = uploadInfo.publicUrl,
                messageType = messageType.name
            )

            // Update local entity with the actual URL
            database.chatMessageDao.upsertMessage(
                entity.copy(content = uploadInfo.publicUrl)
            )

            return webSocketConnector
                .sendMessage(dto.toJsonPayload())
                .onFailure {
                    markAsFailed(messageId)
                }
        }
    }

    private suspend fun markAsFailed(messageId: String) {
        applicationScope.launch {
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                status = ChatMessageDeliveryStatus.FAILED.name
            )
        }.join()
    }

    override suspend fun retryMessage(messageId: String): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val message = database.chatMessageDao.getMessageById(messageId)
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                status = ChatMessageDeliveryStatus.SENDING.name
            )

            val outgoingNewMessage = OutgoingWebSocketDto.NewMessage(
                chatId = message.chatId,
                messageId = messageId,
                content = message.content,
                messageType = message.messageType
            )
            return webSocketConnector
                .sendMessage(outgoingNewMessage.toJsonPayload())
                .onFailure {
                    applicationScope.launch {
                        database.chatMessageDao.upsertMessage(
                            message.copy(
                                deliveryStatus = ChatMessageDeliveryStatus.FAILED.name,
                                timestamp = Clock.System.now().toEpochMilliseconds()
                            )
                        )
                    }.join()
                }
        }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return chatMessageService
            .deleteMessage(messageId)
            .onSuccess {
                applicationScope.launch {
                    database.chatMessageDao.deleteMessageById(messageId)
                }.join()
            }
    }

    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                status = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessages(chatId, before)
            .onSuccess { messages ->
                return safeDatabaseUpdate {
                    database.chatMessageDao.upsertMessagesAndSyncIfNecessary(
                        chatId = chatId,
                        serverMessages = messages.map { it.toEntity() },
                        pageSize = ChatMessageConstants.PAGE_SIZE,
                        shouldSync = before == null // Only sync for most recent page
                    )
                    messages
                }
            }
    }

    override fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>> {
        return database
            .chatMessageDao
            .getMessagesByChatId(chatId)
            .map { messages ->
                messages.map { it.toDomain() }
            }
    }

    private fun OutgoingWebSocketDto.NewMessage.toJsonPayload(): String {
        val webSocketMessage = WebSocketMessageDto(
            type = type.name,
            payload = json.encodeToString(this)
        )
        return json.encodeToString(webSocketMessage)
    }
}