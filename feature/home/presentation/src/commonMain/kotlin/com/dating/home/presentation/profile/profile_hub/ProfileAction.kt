package com.dating.home.presentation.profile.profile_hub

sealed interface ProfileAction {
    data object OnDismiss: ProfileAction
    data object OnUploadPictureClick: ProfileAction
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?): ProfileAction
    data object OnDeletePictureClick: ProfileAction
    data object OnConfirmDeleteClick: ProfileAction
    data object OnDismissDeleteConfirmationDialogClick: ProfileAction
    data object OnToggleCurrentPasswordVisibility: ProfileAction
    data object OnToggleNewPasswordVisibility: ProfileAction
    data object OnChangePasswordClick: ProfileAction
    data object OnLogoutClick: ProfileAction
    data object OnConfirmLogoutClick: ProfileAction
    data object OnDismissLogoutConfirmationDialogClick: ProfileAction
}