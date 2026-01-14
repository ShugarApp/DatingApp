package com.dating.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

enum class RegisterStep {
    Credentials,
    BasicInfo,
    BirthDate,
    GenderInterest,
    LookingFor
}

data class RegisterState(
    // Step 1: Credentials
    val emailTextState: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val emailError: UiText? = null,
    val passwordTextState: TextFieldState = TextFieldState(),
    val isPasswordValid: Boolean = false,
    val passwordError: UiText? = null,
    val isPasswordVisible: Boolean = false,
    
    // Step 2: Basic Info
    val usernameTextState: TextFieldState = TextFieldState(),
    val isUsernameValid: Boolean = false,
    val usernameError: UiText? = null,
    val nameTextState: TextFieldState = TextFieldState(),
    
    // Step 3: Birth Date
    val birthDateTextState: TextFieldState = TextFieldState(), 
    
    // Step 4: Gender & Interest
    val selectedGender: String? = null,
    val selectedInterest: String? = null, 
    
    // Step 5: Looking For
    val selectedLookingFor: String? = null,
    
    val currentStep: RegisterStep = RegisterStep.Credentials,
    
    // General
    val registrationError: UiText? = null,
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val canProceed: Boolean = false
)