package com.dating.home.presentation.profile.verification

sealed interface VerificationEvent {
    data object NavigateBack : VerificationEvent
}
