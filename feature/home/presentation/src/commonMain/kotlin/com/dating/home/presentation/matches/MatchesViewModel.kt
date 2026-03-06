package com.dating.home.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.matching.MatchingService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchesViewModel(
    private val matchingService: MatchingService
) : ViewModel() {

    private val _state = MutableStateFlow(MatchesState())
    val state = _state.asStateFlow()

    private val _events = Channel<MatchesEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadMatches()
    }

    fun onAction(action: MatchesAction) {
        when (action) {
            is MatchesAction.OnRefresh -> loadMatches()
            is MatchesAction.OnMatchClick -> {
                viewModelScope.launch {
                    _events.send(MatchesEvent.NavigateToProfile(action.matchId, action.imageUrl))
                }
            }
            is MatchesAction.OnStartChat -> startChat(action.matchId)
        }
    }

    private fun loadMatches() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            matchingService.getMatches()
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            matches = users.map { user ->
                                Match(
                                    id = user.id,
                                    username = user.username,
                                    profilePictureUrl = user.profilePictureUrl,
                                    city = user.city,
                                    country = user.country
                                )
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun startChat(matchId: String) {
        viewModelScope.launch {
            _events.send(MatchesEvent.NavigateToChat(matchId))
        }
    }
}
