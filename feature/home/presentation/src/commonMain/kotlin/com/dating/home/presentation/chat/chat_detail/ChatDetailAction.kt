package com.dating.home.presentation.chat.chat_detail

import com.dating.home.domain.models.MessageType
import com.dating.home.presentation.chat.model.MessageUi

sealed interface ChatDetailAction {
    data object OnSendMessageClick: ChatDetailAction
    data class OnSendMediaMessage(
        val mediaBytes: ByteArray,
        val mimeType: String,
        val messageType: MessageType
    ): ChatDetailAction
    data class OnSendGifUrl(val url: String): ChatDetailAction
    data object OnToggleMediaPicker: ChatDetailAction
    data object OnScrollToTop: ChatDetailAction
    data class OnSelectChat(val chatId: String?): ChatDetailAction
    data class OnDeleteMessageClick(val message: MessageUi.LocalUserMessage): ChatDetailAction
    data class OnMessageLongClick(val message: MessageUi): ChatDetailAction
    data object OnDismissMessageMenu: ChatDetailAction
    data class OnCopyMessage(val content: String): ChatDetailAction
    data class OnRetryClick(val message: MessageUi.LocalUserMessage): ChatDetailAction
    data object OnBackClick: ChatDetailAction
    data object OnToggleMessageSearch: ChatDetailAction
    data class OnMessageSearchQueryChanged(val query: String): ChatDetailAction
    data object OnNextSearchResult: ChatDetailAction
    data object OnPreviousSearchResult: ChatDetailAction
    data object OnChatOptionsClick: ChatDetailAction
    // TODO: Re-enable OnChatMembersClick when group chat members feature is needed
    data class OnProfileClick(val userId: String): ChatDetailAction
    data object OnLeaveChatClick: ChatDetailAction
    data object OnDismissChatOptions: ChatDetailAction
    data object OnRetryPaginationClick: ChatDetailAction
    data object OnHideBanner: ChatDetailAction
    data class OnFirstVisibleIndexChanged(val index: Int): ChatDetailAction
    data class OnTopVisibleIndexChanged(val topVisibleIndex: Int): ChatDetailAction
    data class OnTextChanged(val text: String): ChatDetailAction
    data object OnBlockUserClick: ChatDetailAction
    data object OnConfirmBlockUser: ChatDetailAction
    data object OnDismissBlockDialog: ChatDetailAction
    data object OnDeleteMatchClick: ChatDetailAction
    data object OnConfirmDeleteMatch: ChatDetailAction
    data object OnDismissDeleteMatchDialog: ChatDetailAction
    data object OnReportUserClick: ChatDetailAction
    data class OnSubmitReport(val reason: com.dating.home.domain.report.ReportReason, val description: String?): ChatDetailAction
    data object OnDismissReportSheet: ChatDetailAction
    data object OnConfirmBlockAfterReport: ChatDetailAction
    data object OnDismissBlockAfterReportDialog: ChatDetailAction
    data class OnReactToMessage(val messageId: String, val emoji: String): ChatDetailAction
    data object OnProposeDateClick: ChatDetailAction
    data object OnDismissDateProposalSheet: ChatDetailAction
    data class OnSubmitDateProposal(val dateTime: String, val location: String): ChatDetailAction
    data class OnAcceptProposal(val messageId: String): ChatDetailAction
    data class OnRejectProposal(val messageId: String): ChatDetailAction
    data class OnCancelProposal(val messageId: String): ChatDetailAction
    data class OnEditProposal(val messageId: String, val dateTime: String, val location: String): ChatDetailAction
}