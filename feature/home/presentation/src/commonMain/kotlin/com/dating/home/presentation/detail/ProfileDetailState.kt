package com.dating.home.presentation.detail

import com.dating.core.domain.auth.User
import com.dating.core.presentation.util.UiText

data class ProfileDetailState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: UiText? = null
)

sealed interface ProfileDetailAction {
    data object OnBack : ProfileDetailAction
    data class OnSwipeRight(val userId: String) : ProfileDetailAction
    data class OnSwipeLeft(val userId: String) : ProfileDetailAction
}

sealed interface ProfileDetailEvent {
    data class NavigateBack(val swipedUserId: String? = null) : ProfileDetailEvent
    data class ShowMatch(val userName: String, val swipedUserId: String) : ProfileDetailEvent
}
