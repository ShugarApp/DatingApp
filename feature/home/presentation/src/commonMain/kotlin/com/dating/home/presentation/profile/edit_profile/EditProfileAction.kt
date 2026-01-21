package com.dating.home.presentation.profile.edit_profile

import org.jetbrains.compose.resources.StringResource

sealed interface EditProfileAction {
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?): EditProfileAction
    data object OnDeletePictureClick: EditProfileAction
    data object OnConfirmDeleteClick: EditProfileAction
    data object OnDismissDeleteConfirmationDialogClick: EditProfileAction
    data class OnInterestSelected(val interest: StringResource): EditProfileAction
    data class OnInterestRemoved(val interest: StringResource): EditProfileAction
}
