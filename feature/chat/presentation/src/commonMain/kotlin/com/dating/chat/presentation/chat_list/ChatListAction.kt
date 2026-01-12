package com.dating.chat.presentation.chat_list

sealed interface ChatListAction {
    data object OnCreateChatClick: ChatListAction
    data class OnSelectChat(val chatId: String?): ChatListAction
}
