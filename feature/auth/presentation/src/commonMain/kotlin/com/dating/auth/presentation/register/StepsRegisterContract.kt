package com.dating.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

data class StepsRegisterState(

    // Step 1: Basic Info
    val usernameTextState: TextFieldState = TextFieldState(),
    val isUsernameValid: Boolean = false,
    val usernameError: UiText? = null,
    val nameTextState: TextFieldState = TextFieldState(),

    // Step 2: Birth Date
    val birthDateTextState: TextFieldState = TextFieldState(),

    // Step 3: Gender & Interest
    val selectedGender: String? = null,
    val selectedInterest: String? = null,

    // Step 4: Looking For
    val selectedLookingFor: String? = null,

    val currentStep: RegisterStep = RegisterStep.BasicInfo,

    // General
    val registrationError: UiText? = null,
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val canProceed: Boolean = false
)

sealed interface StepsRegisterAction {
    data object OnInputTextFocusGain : StepsRegisterAction
    data object OnRegisterClick : StepsRegisterAction
    data object OnNextClick : StepsRegisterAction
    data object OnBackClick : StepsRegisterAction
    data class OnGenderSelect(val gender: String) : StepsRegisterAction
    data class OnInterestSelect(val interest: String) : StepsRegisterAction
    data class OnLookingForSelect(val lookingFor: String) : StepsRegisterAction
}

sealed interface StepsRegisterEvent {
    data class Success(val email: String) : StepsRegisterEvent
    data object OnBack : StepsRegisterEvent
}
