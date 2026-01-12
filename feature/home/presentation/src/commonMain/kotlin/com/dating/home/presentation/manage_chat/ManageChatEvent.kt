package com.dating.home.presentation.manage_chat

sealed interface ManageChatEvent {
    data object OnMembersAdded: ManageChatEvent
}