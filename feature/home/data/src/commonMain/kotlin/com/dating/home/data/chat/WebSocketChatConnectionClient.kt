package com.dating.home.data.chat

import com.dating.core.domain.auth.SessionStorage
import com.dating.home.data.dto.websocket.IncomingWebSocketDto
import com.dating.home.data.dto.websocket.IncomingWebSocketType
import com.dating.home.data.dto.websocket.OutgoingWebSocketDto
import com.dating.home.data.dto.websocket.WebSocketMessageDto
import com.dating.home.data.mappers.toDomain
import com.dating.home.data.mappers.toEntity
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.data.network.KtorWebSocketConnector
import com.dating.home.database.AppChatDatabase
import com.dating.home.domain.chat.ChatConnectionClient
import com.dating.home.domain.chat.ChatRepository
import com.dating.home.domain.models.MessagesReadEvent
import com.dating.home.domain.models.ReactionSummary
import com.dating.home.domain.models.TypingIndicator
import com.dating.home.data.dto.websocket.ReactionSummaryDto
import com.dating.home.database.entities.MessageReactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json

class WebSocketChatConnectionClient(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val database: AppChatDatabase,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : ChatConnectionClient {

    private val parsedMessages = webSocketConnector
        .messages
        .mapNotNull { parseIncomingMessage(it) }
        .onEach { handleIncomingMessage(it) }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000)
        )

    override val chatMessages = parsedMessages
        .filterIsInstance<IncomingWebSocketDto.NewMessageDto>()
        .mapNotNull {
            database.chatMessageDao.getMessageById(it.id)?.toDomain()
        }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000)
        )

    override val typingIndicators = parsedMessages
        .filterIsInstance<IncomingWebSocketDto.TypingIndicatorDto>()
        .mapNotNull { dto ->
            TypingIndicator(
                chatId = dto.chatId,
                userId = dto.userId
            )
        }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000)
        )

    override val messagesRead = parsedMessages
        .filterIsInstance<IncomingWebSocketDto.MessagesReadDto>()
        .mapNotNull { dto ->
            MessagesReadEvent(
                chatId = dto.chatId,
                readByUserId = dto.readByUserId,
                messageIds = dto.messageIds
            )
        }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000)
        )

    override val connectionState = webSocketConnector.connectionState

    override suspend fun sendTyping(chatId: String) {
        val dto = OutgoingWebSocketDto.Typing(chatId = chatId)
        val webSocketMessage = WebSocketMessageDto(
            type = dto.type.name,
            payload = json.encodeToString(dto)
        )
        webSocketConnector.sendMessage(json.encodeToString(webSocketMessage))
    }

    override suspend fun sendReadReceipt(chatId: String, messageIds: List<String>) {
        if (messageIds.isEmpty()) return
        val dto = OutgoingWebSocketDto.ReadMessages(chatId = chatId, messageIds = messageIds)
        val webSocketMessage = WebSocketMessageDto(
            type = dto.type.name,
            payload = json.encodeToString(dto)
        )
        webSocketConnector.sendMessage(json.encodeToString(webSocketMessage))
    }

    override suspend fun sendReaction(messageId: String, chatId: String, emoji: String) {
        val dto = OutgoingWebSocketDto.ReactMessage(
            messageId = messageId,
            chatId = chatId,
            emoji = emoji
        )
        val webSocketMessage = WebSocketMessageDto(
            type = dto.type.name,
            payload = json.encodeToString(dto)
        )
        webSocketConnector.sendMessage(json.encodeToString(webSocketMessage))
    }

    private fun parseIncomingMessage(message: WebSocketMessageDto): IncomingWebSocketDto? {
        return when (message.type) {
            IncomingWebSocketType.NEW_MESSAGE.name -> {
                json.decodeFromString<IncomingWebSocketDto.NewMessageDto>(message.payload)
            }

            IncomingWebSocketType.MESSAGE_DELETED.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessageDeletedDto>(message.payload)
            }

            IncomingWebSocketType.PROFILE_PICTURE_UPDATED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ProfilePictureUpdated>(message.payload)
            }

            IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ChatParticipantsChangedDto>(message.payload)
            }

            IncomingWebSocketType.MESSAGES_READ.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessagesReadDto>(message.payload)
            }

            IncomingWebSocketType.TYPING_INDICATOR.name -> {
                json.decodeFromString<IncomingWebSocketDto.TypingIndicatorDto>(message.payload)
            }

            IncomingWebSocketType.MESSAGE_REACTION_UPDATED.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessageReactionUpdatedDto>(message.payload)
            }

            else -> null
        }
    }

    private suspend fun handleIncomingMessage(message: IncomingWebSocketDto) {
        when (message) {
            is IncomingWebSocketDto.ChatParticipantsChangedDto -> refreshChat(message)
            is IncomingWebSocketDto.MessageDeletedDto -> deleteMessage(message)
            is IncomingWebSocketDto.NewMessageDto -> handleNewMessage(message)
            is IncomingWebSocketDto.ProfilePictureUpdated -> updateProfilePicture(message)
            is IncomingWebSocketDto.MessagesReadDto -> handleMessagesRead(message)
            is IncomingWebSocketDto.TypingIndicatorDto -> Unit
            is IncomingWebSocketDto.MessageReactionUpdatedDto -> handleReactionUpdated(message)
        }
    }

    private suspend fun refreshChat(message: IncomingWebSocketDto.ChatParticipantsChangedDto) {
        chatRepository.fetchChatById(message.chatId)
    }

    private suspend fun deleteMessage(message: IncomingWebSocketDto.MessageDeletedDto) {
        database.chatMessageDao.deleteMessageById(message.messageId)
    }

    private suspend fun handleNewMessage(message: IncomingWebSocketDto.NewMessageDto) {
        val chatExists = database.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatRepository.fetchChatById(message.chatId)
        }

        val entity = message.toEntity()
        database.chatMessageDao.upsertMessage(entity)
    }

    private suspend fun handleMessagesRead(message: IncomingWebSocketDto.MessagesReadDto) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        message.messageIds.forEach { messageId ->
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                status = ChatMessageDeliveryStatus.READ.name,
                timestamp = now
            )
        }
    }

    private suspend fun handleReactionUpdated(message: IncomingWebSocketDto.MessageReactionUpdatedDto) {
        val entities = message.reactions.map { reaction ->
            MessageReactionEntity(
                id = "${message.messageId}_${reaction.emoji}",
                messageId = message.messageId,
                emoji = reaction.emoji,
                count = reaction.count,
                reactedByMe = reaction.reactedByMe
            )
        }

        // Delete existing reactions for this message and replace with server state
        database.messageReactionDao.deleteReactionsForMessage(message.messageId)
        if (entities.isNotEmpty()) {
            database.messageReactionDao.upsertReactions(entities)
        }
    }

    private suspend fun updateProfilePicture(message: IncomingWebSocketDto.ProfilePictureUpdated) {
        database.chatParticipantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl
        )

        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null && authInfo.user.id == message.userId) {
            sessionStorage.set(
                info = authInfo.copy(
                    user = authInfo.user.copy(
                        photos = listOfNotNull(message.newUrl) + authInfo.user.photos.drop(1)
                    )
                )
            )
        }
    }
}
