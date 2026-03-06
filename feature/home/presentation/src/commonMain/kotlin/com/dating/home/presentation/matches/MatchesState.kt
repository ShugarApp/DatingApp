package com.dating.home.presentation.matches

import com.dating.core.presentation.util.UiText

data class MatchesState(
    val isLoading: Boolean = false,
    val matches: List<Match> = emptyList(),
    val error: UiText? = null
)

data class Match(
    val id: String,
    val username: String,
    val profilePictureUrl: String?,
    val city: String?,
    val country: String?
)

sealed interface MatchesAction {
    data object OnRefresh : MatchesAction
    data class OnMatchClick(val matchId: String, val imageUrl: String?) : MatchesAction
    data class OnStartChat(val matchId: String) : MatchesAction
}

sealed interface MatchesEvent {
    data class Error(val error: UiText) : MatchesEvent
    data class NavigateToChat(val chatId: String) : MatchesEvent
    data class NavigateToProfile(val userId: String, val imageUrl: String?) : MatchesEvent
}
