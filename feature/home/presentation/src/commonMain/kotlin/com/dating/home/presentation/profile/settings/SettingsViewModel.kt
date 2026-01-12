package com.dating.home.presentation.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.auth.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authService: AuthService,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _events = Channel<SettingsEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(SettingsState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if (authInfo != null) {
            currentState.copy(
                username = authInfo.user.username
            )
        } else currentState
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnLogoutClick -> showLogoutConfirmation()
            SettingsAction.OnConfirmLogoutClick -> logout()
            SettingsAction.OnDismissLogoutConfirmationDialogClick -> dismissLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        _state.update {
            it.copy(
                showLogoutConfirmationDialog = true
            )
        }
    }

    private fun dismissLogoutConfirmation() {
        _state.update {
            it.copy(
                showLogoutConfirmationDialog = false
            )
        }
    }

    private fun logout() {
        dismissLogoutConfirmation()
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().first()
            val refreshToken = authInfo?.refreshToken ?: return@launch
            authService.logout(refreshToken)
            sessionStorage.set(null)
            _events.send(SettingsEvent.OnLogoutSuccess)
        }
    }
}
