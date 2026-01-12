package com.dating.chat.presentation.chat_list

import com.dating.chat.presentation.model.ChatUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
)