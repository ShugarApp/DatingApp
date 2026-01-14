package com.dating.home.presentation.profile.settings.changepassword

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.error_current_password_equal_to_new_one
import aura.feature.home.presentation.generated.resources.error_current_password_incorrect
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.domain.validation.PasswordValidator
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authService: AuthService
): ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state = _state
        .onStart {
            observeCanChangePassword()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChangePasswordState()
        )

    fun onAction(action: ChangePasswordAction) {
        when(action) {
            is ChangePasswordAction.OnChangePasswordClick -> changePassword()
            is ChangePasswordAction.OnToggleCurrentPasswordVisibility -> {
                _state.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
            }
            is ChangePasswordAction.OnToggleNewPasswordVisibility -> {
                _state.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
            }
        }
    }

    private fun observeCanChangePassword() {
        val isCurrentPasswordValidFlow = snapshotFlow {
            state.value.currentPasswordTextState.text.toString()
        }.map { it.isNotBlank() }.distinctUntilChanged()

        val isNewPasswordValidFlow = snapshotFlow {
            state.value.newPasswordTextState.text.toString()
        }.map {
            PasswordValidator.validate(it).isValidPassword
        }.distinctUntilChanged()

        combine(
            isCurrentPasswordValidFlow,
            isNewPasswordValidFlow
        ) { isCurrentValid, isNewValid ->
            _state.update { it.copy(
                canChangePassword = isCurrentValid && isNewValid
            ) }
        }.launchIn(viewModelScope)
    }

    private fun changePassword() {
        if(!state.value.canChangePassword && state.value.isChangingPassword) {
            return
        }

        _state.update { it.copy(
            isChangingPassword = true,
            isPasswordChangeSuccessful = false
        ) }

        viewModelScope.launch {
            val currentPassword = state.value.currentPasswordTextState.text.toString()
            val newPassword = state.value.newPasswordTextState.text.toString()

            authService
                .changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )
                .onSuccess {
                    state.value.currentPasswordTextState.clearText()
                    state.value.newPasswordTextState.clearText()

                    _state.update { it.copy(
                        isChangingPassword = false,
                        newPasswordError = null,
                        isNewPasswordVisible = false,
                        isCurrentPasswordVisible = false,
                        isPasswordChangeSuccessful = true
                    ) }
                }
                .onFailure { error ->
                    val errorMessage = when(error) {
                        DataError.Remote.UNAUTHORIZED -> {
                            UiText.Resource(Res.string.error_current_password_incorrect)
                        }
                        DataError.Remote.CONFLICT -> {
                            UiText.Resource(Res.string.error_current_password_equal_to_new_one)
                        }
                        else -> error.toUiText()
                    }
                    _state.update { it.copy(
                        newPasswordError = errorMessage,
                        isChangingPassword = false
                    ) }
                }
        }
    }
}

data class ChangePasswordState(
    val currentPasswordTextState: TextFieldState = TextFieldState(),
    val isCurrentPasswordVisible: Boolean = false,
    val newPasswordTextState: TextFieldState = TextFieldState(),
    val isNewPasswordVisible: Boolean = false,
    val canChangePassword: Boolean = false,
    val isChangingPassword: Boolean = false,
    val isPasswordChangeSuccessful: Boolean = false,
    val newPasswordError: UiText? = null
)

sealed interface ChangePasswordAction {
    data object OnChangePasswordClick: ChangePasswordAction
    data object OnToggleCurrentPasswordVisibility: ChangePasswordAction
    data object OnToggleNewPasswordVisibility: ChangePasswordAction
}
