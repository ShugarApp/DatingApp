package com.dating.home.domain.models

data class MessagesReadEvent(
    val chatId: String,
    val readByUserId: String,
    val messageIds: List<String>
)
