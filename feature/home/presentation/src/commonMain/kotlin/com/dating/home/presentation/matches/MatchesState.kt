package com.dating.home.presentation.matches

import com.dating.core.presentation.util.UiText

enum class MatchesTab { MATCHES, LIKES }

data class MatchesState(
    val selectedTab: MatchesTab = MatchesTab.MATCHES,
    val isLoading: Boolean = false,
    val matches: List<Match> = emptyList(),
    val likes: List<Match> = emptyList(),
    val error: UiText? = null
)

data class Match(
    val id: String,
    val username: String,
    val profilePictureUrl: String?,
    val photos: List<String> = emptyList(),
    val city: String?,
    val country: String?
)

sealed interface MatchesAction {
    data object OnRefresh : MatchesAction
    data class OnTabSelected(val tab: MatchesTab) : MatchesAction
    data class OnMatchClick(val matchId: String, val imageUrl: String?) : MatchesAction
    data class OnStartChat(val matchId: String) : MatchesAction
    data class OnLikeClick(val userId: String, val imageUrl: String?) : MatchesAction
}

sealed interface MatchesEvent {
    data class Error(val error: UiText) : MatchesEvent
    data class NavigateToChat(val chatId: String) : MatchesEvent
    data class NavigateToProfile(val userId: String, val imageUrl: String?) : MatchesEvent
}
