package com.dating.home.presentation.home.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.discovery.DiscoveryPreferencesStorage
import com.dating.core.domain.discovery.Gender
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.matching.SwipeAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val matchingService: MatchingService,
    private val discoveryPreferences: DiscoveryPreferencesStorage
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    private val _events = Channel<FeedEvent>()
    val events = _events.receiveAsFlow()

    private var isInitialized = false

    init {
        viewModelScope.launch {
            val prefs = discoveryPreferences.get()
            _state.update {
                it.copy(
                    minAge = prefs.minAge,
                    maxAge = prefs.maxAge,
                    maxDistance = prefs.maxDistance,
                    showMe = prefs.showMe
                )
            }
            loadFeed()
            isInitialized = true
            checkCompleteProfilePrompt()
        }
    }

    private suspend fun checkCompleteProfilePrompt() {
        if (!discoveryPreferences.isCompleteProfilePromptShown()) {
            _state.update { it.copy(showCompleteProfileDialog = true) }
            discoveryPreferences.setCompleteProfilePromptShown()
        }
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
            is FeedAction.OnFiltersApplied -> {
                _state.update {
                    it.copy(
                        maxDistance = action.distance,
                        showMe = action.gender,
                        minAge = action.minAge,
                        maxAge = action.maxAge,
                        feedItems = emptyList()
                    )
                }
                viewModelScope.launch {
                    discoveryPreferences.updateMaxDistance(action.distance)
                    discoveryPreferences.updateShowMe(action.gender)
                    discoveryPreferences.updateAgeRange(action.minAge, action.maxAge)
                }
                loadFeed()
            }
            FeedAction.OnDismissMatchDialog -> {
                _state.update { it.copy(showMatchDialog = false, matchedUserName = null) }
            }
            FeedAction.OnCompleteProfileClick -> {
                _state.update { it.copy(showCompleteProfileDialog = false) }
                viewModelScope.launch {
                    _events.send(FeedEvent.NavigateToEditProfile)
                }
            }
            FeedAction.OnDismissCompleteProfileDialog -> {
                _state.update { it.copy(showCompleteProfileDialog = false) }
            }
            FeedAction.OnScreenResumed -> {
                if (isInitialized) refreshPreferencesIfChanged()
            }
            is FeedAction.OnUserSwiped -> {
                _state.update { current ->
                    current.copy(feedItems = current.feedItems.filter { it.userId != action.userId })
                }
            }
        }
    }

    private fun refreshPreferencesIfChanged() {
        viewModelScope.launch {
            val prefs = discoveryPreferences.get()
            val current = _state.value
            if (prefs.minAge != current.minAge ||
                prefs.maxAge != current.maxAge ||
                prefs.maxDistance != current.maxDistance ||
                prefs.showMe != current.showMe
            ) {
                _state.update {
                    it.copy(
                        minAge = prefs.minAge,
                        maxAge = prefs.maxAge,
                        maxDistance = prefs.maxDistance,
                        showMe = prefs.showMe,
                        feedItems = emptyList()
                    )
                }
                loadFeed()
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasConnectionError = false) }
            val s = _state.value
            val genderFilter = when (s.showMe) {
                Gender.EVERYONE -> null
                else -> s.showMe.apiValue
            }
            matchingService.getFeed(
                gender = genderFilter,
                minAge = s.minAge,
                maxAge = s.maxAge,
                maxDistance = s.maxDistance
            )
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
                .onFailure { _ ->
                    _state.update { it.copy(isLoading = false, hasConnectionError = true) }
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
