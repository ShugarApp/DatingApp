package com.dating.home.presentation.chat.chat_list

sealed interface ChatListAction {
    data object OnCreateChatClick: ChatListAction
    data object OnRefresh: ChatListAction
    data class OnSelectChat(val chatId: String?): ChatListAction
    data object OnToggleSearch: ChatListAction
    data class OnSearchQueryChanged(val query: String): ChatListAction
    data class OnSwipeToDeleteChat(val chatId: String): ChatListAction
    data object OnConfirmDeleteChat: ChatListAction
    data object OnDismissDeleteChatDialog: ChatListAction
}
