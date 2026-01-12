package com.dating.home.presentation.chat.manage_chat

sealed interface ManageChatEvent {
    data object OnMembersAdded: ManageChatEvent
}