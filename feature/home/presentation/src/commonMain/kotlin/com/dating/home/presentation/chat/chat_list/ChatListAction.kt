package com.dating.home.presentation.chat.chat_list

sealed interface ChatListAction {
    data object OnCreateChatClick: ChatListAction
    data class OnSelectChat(val chatId: String?): ChatListAction
}
