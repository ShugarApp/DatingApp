package com.dating.chat.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchesViewModel : ViewModel() {

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
                // TODO: Navigate to match profile
            }
            is MatchesAction.OnStartChat -> {
                startChat(action.matchId)
            }
        }
    }

    private fun loadMatches() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // TODO: Replace with actual API call
            // Simulating matches data
            val mockMatches = listOf(
                Match(
                    id = "match1",
                    userId = "user1",
                    userName = "Ana Martínez",
                    userAge = 28,
                    userImageUrl = null,
                    bio = "Amante del café y los viajes ✈️",
                    matchedAt = 86400000,
                    distance = 5
                ),
                Match(
                    id = "match2",
                    userId = "user2",
                    userName = "Laura Sánchez",
                    userAge = 26,
                    userImageUrl = null,
                    bio = "Fotógrafa profesional 📸",
                    matchedAt = 86400000,
                    distance = 8
                ),
                Match(
                    id = "match3",
                    userId = "user3",
                    userName = "Sofia López",
                    userAge = 30,
                    userImageUrl = null,
                    bio = "Yoga y meditación 🧘‍♀️",
                    matchedAt = 259200000,
                    distance = 3
                )
            )
            
            _state.update {
                it.copy(
                    isLoading = false,
                    matches = mockMatches
                )
            }
        }
    }

    private fun startChat(matchId: String) {
        viewModelScope.launch {
            // TODO: Create or get existing chat with this match
            // For now, just emit an event
            _events.send(MatchesEvent.NavigateToChat(matchId))
        }
    }
}
