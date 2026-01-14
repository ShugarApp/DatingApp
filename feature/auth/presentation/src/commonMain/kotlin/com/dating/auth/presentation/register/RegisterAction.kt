package com.dating.auth.presentation.register

sealed interface RegisterAction {
    data object OnLoginClick: RegisterAction
    data object OnInputTextFocusGain: RegisterAction
    data object OnRegisterClick: RegisterAction
    data object OnTogglePasswordVisibilityClick: RegisterAction
    data object OnNextClick: RegisterAction
    data object OnBackClick: RegisterAction
    data class OnGenderSelect(val gender: String): RegisterAction
    data class OnInterestSelect(val interest: String): RegisterAction
    data class OnLookingForSelect(val lookingFor: String): RegisterAction
}