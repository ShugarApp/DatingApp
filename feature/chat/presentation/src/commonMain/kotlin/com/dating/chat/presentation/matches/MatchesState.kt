package com.dating.chat.presentation.matches

import com.dating.core.presentation.util.UiText

data class MatchesState(
    val isLoading: Boolean = false,
    val matches: List<Match> = emptyList(),
    val error: UiText? = null
)

data class Match(
    val id: String,
    val userId: String,
    val userName: String,
    val userAge: Int,
    val userImageUrl: String?,
    val bio: String,
    val matchedAt: Long,
    val distance: Int? = null
)

sealed interface MatchesAction {
    data object OnRefresh : MatchesAction
    data class OnMatchClick(val matchId: String) : MatchesAction
    data class OnStartChat(val matchId: String) : MatchesAction
}

sealed interface MatchesEvent {
    data class Error(val error: UiText) : MatchesEvent
    data class NavigateToChat(val chatId: String) : MatchesEvent
}
