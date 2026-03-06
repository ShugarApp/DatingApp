package com.dating.home.presentation.home.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.matching.SwipeAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val matchingService: MatchingService
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    private val _events = Channel<FeedEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadFeed()
    }

    fun onAction(action: FeedAction) {
        when (action) {
            is FeedAction.OnRefresh -> loadFeed()
            is FeedAction.OnSwipeRight -> swipe(action.userId, SwipeAction.LIKE)
            is FeedAction.OnSwipeLeft -> swipe(action.userId, SwipeAction.DISLIKE)
            is FeedAction.OnUserClick -> {
                viewModelScope.launch {
                    _events.send(FeedEvent.NavigateToProfile(action.userId, action.imageUrl))
                }
            }
            is FeedAction.OnMaxDistanceChanged -> {
                _state.update { it.copy(maxDistance = action.distance) }
                loadFeed()
            }
            FeedAction.OnDismissMatchDialog -> {
                _state.update { it.copy(showMatchDialog = false, matchedUserName = null) }
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            matchingService.getFeed(maxDistance = _state.value.maxDistance)
                .onSuccess { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            feedItems = users.map { user ->
                                FeedItem(
                                    userId = user.id,
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

    private fun swipe(userId: String, action: SwipeAction) {
        val matchedName = _state.value.feedItems.firstOrNull { it.userId == userId }?.username
        // Remove from feed immediately
        _state.update { current ->
            current.copy(feedItems = current.feedItems.filter { it.userId != userId })
        }
        viewModelScope.launch {
            matchingService.swipe(swipedId = userId, action = action)
                .onSuccess { result ->
                    if (result.isMatch) {
                        _state.update {
                            it.copy(showMatchDialog = true, matchedUserName = matchedName)
                        }
                    }
                }
                .onFailure { /* swipe errors are non-critical, feed already updated */ }
        }
    }
}
