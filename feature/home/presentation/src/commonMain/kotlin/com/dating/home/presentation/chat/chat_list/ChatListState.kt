package com.dating.home.presentation.chat.chat_list

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.presentation.util.UiText
import com.dating.home.presentation.chat.model.ChatUi

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchTextFieldState: TextFieldState = TextFieldState(),
    val showDeleteConfirmationForChatId: String? = null
) {
    val filteredChats: List<ChatUi>
        get() = if (searchQuery.isBlank()) {
            chats
        } else {
            chats.filter { chat ->
                chat.otherParticipants.any { participant ->
                    participant.username.contains(searchQuery, ignoreCase = true)
                }
            }
        }
}
