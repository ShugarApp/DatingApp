package com.dating.home.presentation.chat.chat_list

import com.dating.home.presentation.chat.model.ChatUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
)