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
            loadFeed(page = 0, isInitialLoad = true)
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
            is FeedAction.OnRefresh -> resetAndLoadFeed()
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
                        feedItems = emptyList(),
                        currentPage = 0,
                        hasMore = true
                    )
                }
                viewModelScope.launch {
                    discoveryPreferences.updateMaxDistance(action.distance)
                    discoveryPreferences.updateShowMe(action.gender)
                    discoveryPreferences.updateAgeRange(action.minAge, action.maxAge)
                }
                loadFeed(page = 0, isInitialLoad = true)
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
                val swipedItem = _state.value.feedItems.firstOrNull { it.userId == action.userId }
                _state.update { current ->
                    current.copy(
                        feedItems = current.feedItems.filter { it.userId != action.userId },
                        lastDislikedItem = if (action.isDislike) swipedItem else null
                    )
                }
                prefetchIfNeeded()
            }
            FeedAction.OnUndoSwipe -> undoSwipe()
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
                        feedItems = emptyList(),
                        currentPage = 0,
                        hasMore = true
                    )
                }
                loadFeed(page = 0, isInitialLoad = true)
            }
        }
    }

    private fun resetAndLoadFeed() {
        _state.update {
            it.copy(
                feedItems = emptyList(),
                currentPage = 0,
                hasMore = true,
                lastDislikedItem = null
            )
        }
        loadFeed(page = 0, isInitialLoad = true)
    }

    private fun loadFeed(page: Int, isInitialLoad: Boolean) {
        viewModelScope.launch {
            if (isInitialLoad) {
                _state.update { it.copy(isLoading = true, hasConnectionError = false) }
            } else {
                _state.update { it.copy(isFetchingMore = true) }
            }
            val s = _state.value
            val genderFilter = when (s.showMe) {
                Gender.EVERYONE -> null
                else -> s.showMe.apiValue
            }
            matchingService.getFeed(
                gender = genderFilter,
                minAge = s.minAge,
                maxAge = s.maxAge,
                maxDistance = s.maxDistance,
                page = page,
                size = PAGE_SIZE
            )
                .onSuccess { users ->
                    val newItems = users.map { user ->
                        FeedItem(
                            userId = user.id,
                            username = user.username,
                            profilePictureUrl = user.profilePictureUrl,
                            city = user.city,
                            country = user.country
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isFetchingMore = false,
                            feedItems = if (isInitialLoad) newItems else it.feedItems + newItems,
                            currentPage = page,
                            hasMore = newItems.isNotEmpty()
                        )
                    }
                }
                .onFailure { _ ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isFetchingMore = false,
                            hasConnectionError = isInitialLoad
                        )
                    }
                }
        }
    }

    private fun prefetchIfNeeded() {
        val s = _state.value
        if (s.feedItems.size <= PREFETCH_THRESHOLD && s.hasMore && !s.isFetchingMore && !s.isLoading) {
            loadFeed(page = s.currentPage + 1, isInitialLoad = false)
        }
    }

    private fun swipe(userId: String, action: SwipeAction) {
        val currentItems = _state.value.feedItems
        val swipedItem = currentItems.firstOrNull { it.userId == userId }
        val matchedName = swipedItem?.username
        _state.update { current ->
            current.copy(
                feedItems = current.feedItems.filter { it.userId != userId },
                lastDislikedItem = if (action == SwipeAction.DISLIKE) swipedItem else null
            )
        }
        prefetchIfNeeded()
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

    private fun undoSwipe() {
        val lastDisliked = _state.value.lastDislikedItem ?: return
        _state.update { it.copy(isUndoing = true) }
        viewModelScope.launch {
            matchingService.undoSwipe(swipedId = lastDisliked.userId)
                .onSuccess {
                    _state.update { current ->
                        current.copy(
                            feedItems = listOf(lastDisliked) + current.feedItems,
                            lastDislikedItem = null,
                            isUndoing = false
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isUndoing = false) }
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_THRESHOLD = 5
    }
}
