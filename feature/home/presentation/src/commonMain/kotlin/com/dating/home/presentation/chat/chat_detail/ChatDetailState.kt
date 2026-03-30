package com.dating.home.presentation.chat.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.home.domain.models.ConnectionState
import com.dating.home.presentation.chat.model.ChatUi
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.core.presentation.util.UiText

data class ChatDetailState(
    val chatUi: ChatUi? = null,
    val isLoading: Boolean = false,
    val messages: List<MessageUi> = emptyList(),
    val error: UiText? = null,
    val messageTextFieldState: TextFieldState = TextFieldState(),
    val canSendMessage: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val paginationError: UiText? = null,
    val endReached: Boolean = false,
    val messageWithOpenMenu: MessageUi? = null,
    val bannerState: BannerState = BannerState(),
    val isChatOptionsOpen: Boolean = false,
    val isNearBottom: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val typingUsernames: List<String> = emptyList(),
    val showBlockDialog: Boolean = false,
    val isBlocking: Boolean = false,
    val showDeleteMatchDialog: Boolean = false,
    val isDeletingMatch: Boolean = false,
    val showReportSheet: Boolean = false,
    val isSubmittingReport: Boolean = false,
    val showBlockAfterReportDialog: Boolean = false,
    val isSearchMode: Boolean = false,
    val messageSearchQuery: String = "",
    val messageSearchResults: List<Int> = emptyList(),
    val currentSearchResultIndex: Int = -1,
    val isMediaPickerOpen: Boolean = false,
    val isUploadingMedia: Boolean = false
)

data class BannerState(
    val formattedDate: UiText? = null,
    val isVisible: Boolean = false
)