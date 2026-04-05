package com.dating.home.presentation.profile.verification

import com.dating.home.presentation.profile.mediapicker.PickedImageData

sealed interface VerificationAction {
    data object OnBack : VerificationAction
    data object OnStartCapture : VerificationAction
    data class OnSelfieCaptured(val imageData: PickedImageData) : VerificationAction
    data object OnRetry : VerificationAction
    data object OnDismissError : VerificationAction
    data class OnPermissionDenied(val permanently: Boolean) : VerificationAction
}
