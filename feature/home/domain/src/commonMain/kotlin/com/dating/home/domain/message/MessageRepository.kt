package com.dating.home.domain.message

import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.MessageType
import com.dating.home.domain.models.MessageWithSender
import com.dating.home.domain.models.OutgoingNewMessage
import com.dating.home.domain.models.ReactionSummary
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>

    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError>

    suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError>

    suspend fun sendMediaMessage(
        chatId: String,
        messageId: String,
        mediaBytes: ByteArray,
        mimeType: String,
        messageType: MessageType
    ): EmptyResult<DataError>

    suspend fun retryMessage(messageId: String): EmptyResult<DataError>

    suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote>

    fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>>

    suspend fun reactToMessage(messageId: String, chatId: String, emoji: String)

    fun getReactionsForMessage(messageId: String): Flow<List<ReactionSummary>>

    fun getReactionsMapForChat(chatId: String): Flow<Map<String, List<ReactionSummary>>>
}