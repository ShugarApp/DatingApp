package com.dating.home.presentation.chat.create_chat

import com.dating.home.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat): CreateChatEvent
}