package com.dating.auth.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val authService: AuthService,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<OnboardingEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnGoogleIdTokenReceived -> loginWithGoogle(action.idToken)
            OnboardingAction.OnGoogleSignInError -> {
                _state.update { it.copy(isGoogleLoading = false) }
            }
            else -> Unit
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isGoogleLoading = true) }

            authService
                .loginWithGoogle(idToken)
                .onSuccess { result ->
                    _state.update { it.copy(isGoogleLoading = false) }

                    if (result.isNewUser) {
                        eventChannel.send(OnboardingEvent.GoogleNewUser(idToken))
                    } else {
                        sessionStorage.set(result.authInfo!!)
                        eventChannel.send(OnboardingEvent.GoogleSuccess)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isGoogleLoading = false)
                    }
                }
        }
    }
}

data class OnboardingState(
    val isGoogleLoading: Boolean = false
)

sealed interface OnboardingEvent {
    data object GoogleSuccess : OnboardingEvent
    data class GoogleNewUser(val idToken: String) : OnboardingEvent
}

sealed interface OnboardingAction {
    data class OnGoogleIdTokenReceived(val idToken: String) : OnboardingAction
    data object OnGoogleSignInError : OnboardingAction
    data object OnLoginClick : OnboardingAction
    data object OnCreateAccountClick : OnboardingAction
}
