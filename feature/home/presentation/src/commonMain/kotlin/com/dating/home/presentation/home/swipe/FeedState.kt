package com.dating.home.presentation.home.swipe

import com.dating.core.presentation.util.UiText

data class FeedState(
    val isLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val error: UiText? = null,
    val maxDistance: Double? = null,
    val minAge: Int = 18,
    val maxAge: Int = 50,
    val showMatchDialog: Boolean = false,
    val matchedUserName: String? = null
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
    data class OnMaxDistanceChanged(val distance: Double?) : FeedAction
    data class OnAgeRangeChanged(val minAge: Int, val maxAge: Int) : FeedAction
    data object OnDismissMatchDialog : FeedAction
}

sealed interface FeedEvent {
    data class Error(val error: UiText) : FeedEvent
    data class NavigateToProfile(val userId: String, val imageUrl: String?) : FeedEvent
}
