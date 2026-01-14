package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.home.domain.participant.ChatParticipantRepository
import com.dating.core.domain.auth.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val chatParticipantRepository: ChatParticipantRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if(authInfo != null) {
            currentState.copy(
                username = authInfo.user.username,
                userInitials = authInfo.user.username.take(2),
                emailTextState = TextFieldState(initialText = authInfo.user.email),
                profilePictureUrl = authInfo.user.profilePictureUrl,
            )
        } else currentState
    }
        .onStart {
            if (!hasLoadedInitialData) {
                fetchLocalParticipantDetails()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        // No actions needed for now
    }

    private fun fetchLocalParticipantDetails() {
        viewModelScope.launch {
            chatParticipantRepository.fetchLocalParticipant()
        }
    }
}