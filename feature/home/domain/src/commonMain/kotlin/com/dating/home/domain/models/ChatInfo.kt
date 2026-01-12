package com.dating.home.domain.models

data class ChatInfo(
    val chat: Chat,
    val messages: List<MessageWithSender>
)
