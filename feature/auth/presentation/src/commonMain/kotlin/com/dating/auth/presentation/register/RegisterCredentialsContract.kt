package com.dating.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.dating.core.presentation.util.UiText

data class RegisterCredentialsState(
    val emailTextState: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val emailError: UiText? = null,
    val passwordTextState: TextFieldState = TextFieldState(),
    val isPasswordValid: Boolean = false,
    val passwordError: UiText? = null,
    val isPasswordVisible: Boolean = false,
    val canProceed: Boolean = false
)

sealed interface RegisterCredentialsAction {
    data object OnLoginClick : RegisterCredentialsAction
    data object OnInputTextFocusGain : RegisterCredentialsAction
    data object OnTogglePasswordVisibilityClick : RegisterCredentialsAction
    data object OnNextClick : RegisterCredentialsAction
    data object OnBackClick : RegisterCredentialsAction
}

sealed interface RegisterCredentialsEvent {
    data class OnNext(val email: String, val password: String) : RegisterCredentialsEvent
    data object OnBack : RegisterCredentialsEvent
    data object OnLogin : RegisterCredentialsEvent
}
