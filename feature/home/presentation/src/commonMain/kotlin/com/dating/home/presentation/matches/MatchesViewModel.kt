package com.dating.home.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.chat.ChatRepository
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.matching.SwipeAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchesViewModel(
    private val matchingService: MatchingService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MatchesState())
    val state = _state.asStateFlow()

    private val _events = Channel<MatchesEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadData()
    }

    fun onAction(action: MatchesAction) {
        when (action) {
            is MatchesAction.OnRefresh -> loadData()
            is MatchesAction.OnTabSelected -> {
                _state.update { it.copy(selectedTab = action.tab) }
            }
            is MatchesAction.OnMatchClick -> {
                viewModelScope.launch {
                    _events.send(MatchesEvent.NavigateToProfile(action.matchId, action.imageUrl))
                }
            }
            is MatchesAction.OnStartChat -> startChat(action.matchId)
            is MatchesAction.OnLikeClick -> {
                viewModelScope.launch {
                    _events.send(MatchesEvent.NavigateToProfile(action.userId, action.imageUrl))
                }
            }
            is MatchesAction.OnLikeUser -> swipeLike(action.userId, SwipeAction.LIKE)
            is MatchesAction.OnDislikeUser -> swipeLike(action.userId, SwipeAction.DISLIKE)
            is MatchesAction.OnDeleteMatchClick -> {
                _state.update { it.copy(showDeleteMatchDialog = true, matchToDelete = action.match) }
            }
            MatchesAction.OnConfirmDeleteMatch -> confirmDeleteMatch()
            MatchesAction.OnDismissDeleteMatchDialog -> {
                _state.update { it.copy(showDeleteMatchDialog = false, matchToDelete = null) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            launch {
                matchingService.getMatches()
                    .onSuccess { users ->
                        _state.update {
                            it.copy(
                                matches = users.map { user -> user.toMatch() }
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.toUiText()) }
                    }
            }

            launch {
                matchingService.getLikes()
                    .onSuccess { users ->
                        _state.update {
                            it.copy(
                                likes = users.map { user -> user.toMatch() }
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.toUiText()) }
                    }
            }
        }.invokeOnCompletion {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun com.dating.core.domain.auth.User.toMatch() = Match(
        id = id,
        username = username,
        profilePictureUrl = profilePictureUrl,
        photos = photos,
        city = city,
        country = country
    )

    private fun confirmDeleteMatch() {
        val match = _state.value.matchToDelete ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeletingMatch = true) }
            matchingService.deleteMatch(match.id)
                .onSuccess {
                    _state.update { state ->
                        state.copy(
                            matches = state.matches.filter { it.id != match.id },
                            showDeleteMatchDialog = false,
                            matchToDelete = null,
                            isDeletingMatch = false
                        )
                    }
                    _events.send(MatchesEvent.MatchDeleted)
                }
                .onFailure { error ->
                    _state.update { it.copy(showDeleteMatchDialog = false, matchToDelete = null, isDeletingMatch = false) }
                    _events.send(MatchesEvent.Error(error.toUiText()))
                }
        }
    }

    private fun swipeLike(userId: String, action: SwipeAction) {
        viewModelScope.launch {
            val likedUser = _state.value.likes.find { it.id == userId }
            _state.update { state ->
                val updatedLikes = state.likes.filter { it.id != userId }
                if (action == SwipeAction.LIKE && likedUser != null) {
                    state.copy(
                        likes = updatedLikes,
                        matches = state.matches + likedUser
                    )
                } else {
                    state.copy(likes = updatedLikes)
                }
            }
            matchingService.swipe(userId, action)
                .onFailure { error ->
                    _events.send(MatchesEvent.Error(error.toUiText()))
                    loadData()
                }
        }
    }

    private fun startChat(matchId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingChat = true) }

            val existingChat = chatRepository.getChats().first()
                .firstOrNull { chat ->
                    chat.participants.any { it.userId == matchId }
                }

            if (existingChat != null) {
                _events.send(MatchesEvent.NavigateToChat(existingChat.id))
            } else {
                chatRepository.createChat(otherUserIds = listOf(matchId))
                    .onSuccess { chat ->
                        _events.send(MatchesEvent.NavigateToChat(chat.id))
                    }
                    .onFailure { error ->
                        _events.send(MatchesEvent.Error(error.toUiText()))
                    }
            }

            _state.update { it.copy(isCreatingChat = false) }
        }
    }
}
