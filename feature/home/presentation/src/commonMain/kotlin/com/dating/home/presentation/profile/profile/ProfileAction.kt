package com.dating.home.presentation.profile.profile

sealed interface ProfileAction {
    data object OnUpdateLocation : ProfileAction
    data object OnAvatarClick : ProfileAction
    data object OnDismissPreview : ProfileAction
}
