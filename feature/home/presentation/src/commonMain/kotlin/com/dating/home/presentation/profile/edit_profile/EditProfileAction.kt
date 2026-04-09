package com.dating.home.presentation.profile.edit_profile

sealed interface EditProfileAction {
    data class OnPictureSelected(val bytes: ByteArray, val mimeType: String?, val slotIndex: Int) : EditProfileAction
    data class OnDeletePhotoClick(val slotIndex: Int) : EditProfileAction
    data object OnConfirmDeletePhoto : EditProfileAction
    data object OnDismissDeleteConfirmationDialogClick : EditProfileAction
    data class OnInterestToggled(val interest: String) : EditProfileAction
    data object OnSaveProfile : EditProfileAction
    // Gender and birthDate are read-only in EditProfileScreen but still needed by ProfileSetupScreen
    data class OnGenderChanged(val gender: String?) : EditProfileAction
    data class OnBirthDateChanged(val birthDate: String?) : EditProfileAction
    data class OnHeightChanged(val height: Int?) : EditProfileAction
    data class OnZodiacChanged(val zodiac: String?) : EditProfileAction
    data class OnSmokingChanged(val smoking: String?) : EditProfileAction
    data class OnDrinkingChanged(val drinking: String?) : EditProfileAction
    data class OnInterestedInChanged(val interestedIn: String?) : EditProfileAction
    data class OnLookingForChanged(val lookingFor: String?) : EditProfileAction
    data class OnIdealDateChanged(val idealDate: String?) : EditProfileAction
    data class OnPhotosReordered(val newPhotos: List<String?>) : EditProfileAction
    data object OnDismissSuccessMessage : EditProfileAction
    data class OnPhotoUploaded(val slotIndex: Int, val publicUrl: String) : EditProfileAction
    data class OnPhotoUploadFailed(val slotIndex: Int) : EditProfileAction
}
