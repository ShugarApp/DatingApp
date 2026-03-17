package com.dating.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import shugar.feature.auth.presentation.generated.resources.Res
import shugar.feature.auth.presentation.generated.resources.error_invalid_email
import shugar.feature.auth.presentation.generated.resources.error_invalid_password
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
            initialValue = RegisterCredentialsState()
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()

    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isPasswordValidFlow
        ) { isEmailValid: Boolean, isPasswordValid: Boolean ->
            _state.update {
                it.copy(
                    isEmailValid = isEmailValid,
                    isPasswordValid = isPasswordValid,
                    canProceed = isEmailValid && isPasswordValid
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

            RegisterCredentialsAction.OnInputTextFocusGain -> {
                clearErrors()
            }
        }
    }

    private fun onNextClick() {
        val currentState = state.value
        val email = currentState.emailTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidation = PasswordValidator.validate(password)

        if (isEmailValid && passwordValidation.isValidPassword) {
            viewModelScope.launch {
                eventChannel.send(RegisterCredentialsEvent.OnNext(email, password))
            }
        } else {
            val emailError = if (!isEmailValid) UiText.Resource(Res.string.error_invalid_email) else null
            val passwordError = if (!passwordValidation.isValidPassword) UiText.Resource(Res.string.error_invalid_password) else null

            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
        }
    }

    private fun clearErrors() {
        _state.update {
            it.copy(
                emailError = null,
                passwordError = null
            )
        }
    }
}
