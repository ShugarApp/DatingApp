package com.dating.home.presentation.profile.edit_profile

sealed interface EditProfileAction {
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?): EditProfileAction
    data object OnDeletePictureClick: EditProfileAction
    data object OnConfirmDeleteClick: EditProfileAction
    data object OnDismissDeleteConfirmationDialogClick: EditProfileAction
    data class OnInterestSelected(val interest: String): EditProfileAction
    data class OnInterestRemoved(val interest: String): EditProfileAction
}
