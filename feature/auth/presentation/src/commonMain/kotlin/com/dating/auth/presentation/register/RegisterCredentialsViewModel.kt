package com.dating.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.error_invalid_email
import aura.feature.auth.presentation.generated.resources.error_invalid_password
import aura.feature.auth.presentation.generated.resources.error_passwords_do_not_match
import com.dating.core.domain.validation.PasswordValidator
import com.dating.core.presentation.util.UiText
import com.dating.domain.EmailValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterCredentialsViewModel : ViewModel() {

    private val eventChannel = Channel<RegisterCredentialsEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterCredentialsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()

    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

    private val isConfirmPasswordMatchingFlow = combine(
        snapshotFlow { state.value.passwordTextState.text.toString() },
        snapshotFlow { state.value.confirmPasswordTextState.text.toString() }
    ) { password, confirmPassword ->
        confirmPassword.isNotEmpty() && confirmPassword == password
    }.distinctUntilChanged()

    private fun observeValidationStates() {
        combine(isEmailValidFlow, isPasswordValidFlow) { isEmailValid, isPasswordValid ->
            isEmailValid to isPasswordValid
        }.combine(isConfirmPasswordMatchingFlow) { (isEmailValid, isPasswordValid), isConfirmPasswordMatching ->
            _state.update {
                it.copy(
                    isEmailValid = isEmailValid,
                    isPasswordValid = isPasswordValid,
                    canProceed = isEmailValid && isPasswordValid && isConfirmPasswordMatching
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegisterCredentialsAction) {
        when (action) {
            RegisterCredentialsAction.OnLoginClick -> {
                viewModelScope.launch { eventChannel.send(RegisterCredentialsEvent.OnLogin) }
            }

            RegisterCredentialsAction.OnNextClick -> onNextClick()
            RegisterCredentialsAction.OnBackClick -> {
                viewModelScope.launch { eventChannel.send(RegisterCredentialsEvent.OnBack) }
            }

            RegisterCredentialsAction.OnTogglePasswordVisibilityClick -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            RegisterCredentialsAction.OnToggleConfirmPasswordVisibilityClick -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }

            RegisterCredentialsAction.OnInputTextFocusGain -> {
                clearErrors()
            }
        }
    }

    private fun onNextClick() {
        val currentState = state.value
        val email = currentState.emailTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()
        val confirmPassword = currentState.confirmPasswordTextState.text.toString()

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidation = PasswordValidator.validate(password)
        val doPasswordsMatch = confirmPassword == password

        if (isEmailValid && passwordValidation.isValidPassword && doPasswordsMatch) {
            viewModelScope.launch {
                eventChannel.send(RegisterCredentialsEvent.OnNext(email, password))
            }
        } else {
            val emailError = if (!isEmailValid) UiText.Resource(Res.string.error_invalid_email) else null
            val passwordError = if (!passwordValidation.isValidPassword) UiText.Resource(Res.string.error_invalid_password) else null
            val confirmPasswordError = if (!doPasswordsMatch) UiText.Resource(Res.string.error_passwords_do_not_match) else null

            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                emailError = null,
                passwordError = null,
                confirmPasswordError = null
            )
        }
    }
}
