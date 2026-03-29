package com.dating.home.data.dto.websocket

import kotlinx.serialization.Serializable

enum class IncomingWebSocketType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    MESSAGES_READ,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    TYPING_INDICATOR
}

@Serializable
sealed interface IncomingWebSocketDto {

    @Serializable
    data class NewMessageDto(
        val id: String,
        val chatId: String,
        val content: String,
        val senderId: String,
        val createdAt: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.NEW_MESSAGE
    ): IncomingWebSocketDto

    @Serializable
    data class MessageDeletedDto(
        val messageId: String,
        val chatId: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.MESSAGE_DELETED
    ): IncomingWebSocketDto

    @Serializable
    data class ProfilePictureUpdated(
        val userId: String,
        val newUrl: String?,
        val type: IncomingWebSocketType = IncomingWebSocketType.PROFILE_PICTURE_UPDATED
    ): IncomingWebSocketDto

    @Serializable
    data class ChatParticipantsChangedDto(
        val chatId: String,
        val type: IncomingWebSocketType = IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED
    ): IncomingWebSocketDto

    @Serializable
    data class MessagesReadDto(
        val chatId: String,
        val readByUserId: String,
        val messageIds: List<String>,
        val type: IncomingWebSocketType = IncomingWebSocketType.MESSAGES_READ
    ): IncomingWebSocketDto

    @Serializable
    data class TypingIndicatorDto(
        val chatId: String,
        val userId: String,
        val isTyping: Boolean,
        val type: IncomingWebSocketType = IncomingWebSocketType.TYPING_INDICATOR
    ): IncomingWebSocketDto
}