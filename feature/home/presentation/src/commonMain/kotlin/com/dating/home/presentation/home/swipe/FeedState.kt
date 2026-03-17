package com.dating.home.presentation.home.swipe

import com.dating.core.domain.discovery.Gender

data class FeedState(
    val isLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val hasConnectionError: Boolean = false,
    val maxDistance: Double? = null,
    val showMe: Gender = Gender.WOMEN,
    val minAge: Int = 18,
    val maxAge: Int = 50,
    val showMatchDialog: Boolean = false,
    val matchedUserName: String? = null,
    val showCompleteProfileDialog: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isFetchingMore: Boolean = false
)

data class FeedItem(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    val city: String?,
    val country: String?
)

sealed interface FeedAction {
    data object OnRefresh : FeedAction
    data class OnSwipeRight(val userId: String) : FeedAction
    data class OnSwipeLeft(val userId: String) : FeedAction
    data class OnUserClick(val userId: String, val imageUrl: String?) : FeedAction
    data class OnFiltersApplied(
        val distance: Double?,
        val gender: Gender,
        val minAge: Int,
        val maxAge: Int
    ) : FeedAction
    data object OnDismissMatchDialog : FeedAction
    data object OnCompleteProfileClick : FeedAction
    data object OnDismissCompleteProfileDialog : FeedAction
    data object OnScreenResumed : FeedAction
    data class OnUserSwiped(val userId: String) : FeedAction
}

sealed interface FeedEvent {
    data class NavigateToProfile(val userId: String, val imageUrl: String?) : FeedEvent
    data object NavigateToEditProfile : FeedEvent
}
