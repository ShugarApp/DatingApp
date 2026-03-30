package com.dating.home.data.dto.websocket

import kotlinx.serialization.Serializable

enum class OutgoingWebSocketType {
    NEW_MESSAGE,
    TYPING,
    READ_MESSAGES,
    REACT_MESSAGE
}

@Serializable
sealed class OutgoingWebSocketDto(
    val type: OutgoingWebSocketType
) {

    @Serializable
    data class NewMessage(
        val chatId: String,
        val messageId: String,
        val content: String,
        val messageType: String = "TEXT"
    ) : OutgoingWebSocketDto(OutgoingWebSocketType.NEW_MESSAGE)

    @Serializable
    data class Typing(
        val chatId: String
    ) : OutgoingWebSocketDto(OutgoingWebSocketType.TYPING)

    @Serializable
    data class ReadMessages(
        val chatId: String,
        val messageIds: List<String>
    ) : OutgoingWebSocketDto(OutgoingWebSocketType.READ_MESSAGES)

    @Serializable
    data class ReactMessage(
        val messageId: String,
        val chatId: String,
        val emoji: String
    ) : OutgoingWebSocketDto(OutgoingWebSocketType.REACT_MESSAGE)
}
