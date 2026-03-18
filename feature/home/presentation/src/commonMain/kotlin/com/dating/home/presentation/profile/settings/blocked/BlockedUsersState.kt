package com.dating.home.presentation.profile.settings.blocked

import com.dating.core.presentation.util.UiText
import com.dating.home.domain.block.BlockedUser

data class BlockedUsersState(
    val isLoading: Boolean = false,
    val blockedUsers: List<BlockedUser> = emptyList(),
    val error: UiText? = null,
    val showUnblockDialog: Boolean = false,
    val userToUnblock: BlockedUser? = null,
    val isUnblocking: Boolean = false
)

sealed interface BlockedUsersAction {
    data object OnRefresh : BlockedUsersAction
    data class OnUnblockClick(val user: BlockedUser) : BlockedUsersAction
    data object OnConfirmUnblock : BlockedUsersAction
    data object OnDismissUnblockDialog : BlockedUsersAction
}

sealed interface BlockedUsersEvent {
    data class ShowToast(val message: UiText) : BlockedUsersEvent
}
