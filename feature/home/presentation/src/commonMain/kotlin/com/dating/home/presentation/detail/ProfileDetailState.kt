package com.dating.home.presentation.detail

import com.dating.core.domain.auth.User
import com.dating.core.presentation.util.UiText

data class ProfileDetailState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: UiText? = null,
    val showBlockDialog: Boolean = false,
    val isBlocking: Boolean = false,
    val showDeleteMatchDialog: Boolean = false,
    val isDeletingMatch: Boolean = false,
    val showReportSheet: Boolean = false,
    val isSubmittingReport: Boolean = false,
    val showBlockAfterReportDialog: Boolean = false
)

sealed interface ProfileDetailAction {
    data object OnBack : ProfileDetailAction
    data class OnSwipeRight(val userId: String) : ProfileDetailAction
    data class OnSwipeLeft(val userId: String) : ProfileDetailAction
    data class OnBlockClick(val userId: String) : ProfileDetailAction
    data object OnConfirmBlock : ProfileDetailAction
    data object OnDismissBlockDialog : ProfileDetailAction
    data class OnDeleteMatchClick(val userId: String) : ProfileDetailAction
    data object OnConfirmDeleteMatch : ProfileDetailAction
    data object OnDismissDeleteMatchDialog : ProfileDetailAction
    data class OnReportClick(val userId: String) : ProfileDetailAction
    data class OnSubmitReport(val reason: com.dating.home.domain.report.ReportReason, val description: String?) : ProfileDetailAction
    data object OnDismissReportSheet : ProfileDetailAction
    data object OnConfirmBlockAfterReport : ProfileDetailAction
    data object OnDismissBlockAfterReportDialog : ProfileDetailAction
}

sealed interface ProfileDetailEvent {
    data class NavigateBack(val swipedUserId: String? = null, val isDislike: Boolean = false) : ProfileDetailEvent
    data class ShowMatch(val userName: String, val swipedUserId: String) : ProfileDetailEvent
    data object OnUserBlocked : ProfileDetailEvent
    data object OnMatchDeleted : ProfileDetailEvent
    data class OnReportSuccess(val message: String) : ProfileDetailEvent
    data class OnReportError(val message: String) : ProfileDetailEvent
    data object OnForceLogout : ProfileDetailEvent
}
