package com.dating.home.data.mappers

import com.dating.home.data.dto.ChatMessageDto
import com.dating.home.data.dto.MediaUploadUrlDto
import com.dating.home.data.dto.websocket.IncomingWebSocketDto
import com.dating.home.data.dto.websocket.OutgoingWebSocketDto
import com.dating.home.database.entities.ChatMessageEntity
import com.dating.home.database.view.LastMessageView
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.MediaUploadInfo
import com.dating.home.domain.models.MessageType
import com.dating.home.domain.models.OutgoingNewMessage
import kotlin.time.Clock
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = Instant.parse(createdAt),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.SENT,
        messageType = parseMessageType(messageType)
    )
}

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(deliveryStatus),
        messageType = parseMessageType(messageType)
    )
}

fun LastMessageView.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.deliveryStatus),
        messageType = parseMessageType(messageType)
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        deliveryStatus = deliveryStatus.name,
        messageType = messageType.name
    )
}

fun ChatMessage.toLastMessageView(): LastMessageView {
    return LastMessageView(
        messageId = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        deliveryStatus = deliveryStatus.name,
        messageType = messageType.name,
        senderUsername = null
    )
}

fun ChatMessage.toNewMessage(): OutgoingWebSocketDto.NewMessage {
    return OutgoingWebSocketDto.NewMessage(
        messageId = id,
        chatId = chatId,
        content = content,
        messageType = messageType.name
    )
}

fun IncomingWebSocketDto.NewMessageDto.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        timestamp = Instant.parse(createdAt).toEpochMilliseconds(),
        deliveryStatus = ChatMessageDeliveryStatus.SENT.name,
        messageType = messageType
    )
}

fun OutgoingNewMessage.toWebSocketDto(): OutgoingWebSocketDto.NewMessage {
    return OutgoingWebSocketDto.NewMessage(
        chatId = chatId,
        messageId = messageId,
        content = content,
        messageType = messageType.name
    )
}

fun OutgoingWebSocketDto.NewMessage.toEntity(
    senderId: String,
    deliveryStatus: ChatMessageDeliveryStatus
): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = messageId,
        chatId = chatId,
        content = content,
        senderId = senderId,
        deliveryStatus = deliveryStatus.name,
        timestamp = Clock.System.now().toEpochMilliseconds(),
        messageType = messageType
    )
}

fun MediaUploadUrlDto.toDomain(): MediaUploadInfo {
    return MediaUploadInfo(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt
    )
}

private fun parseMessageType(value: String): MessageType {
    return try {
        MessageType.valueOf(value)
    } catch (_: IllegalArgumentException) {
        MessageType.TEXT
    }
}
