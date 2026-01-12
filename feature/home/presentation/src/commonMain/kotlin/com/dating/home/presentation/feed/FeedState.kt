package com.dating.home.presentation.feed

import com.dating.core.presentation.util.UiText

data class FeedState(
    val isLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val error: UiText? = null
)

data class FeedItem(
    val id: String,
    val userId: String,
    val userName: String,
    val userImageUrl: String?,
    val content: String,
    val imageUrl: String?,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

sealed interface FeedAction {
    data object OnRefresh : FeedAction
    data class OnLikePost(val postId: String) : FeedAction
    data class OnPass(val postId: String) : FeedAction
    data class OnUserClick(val userId: String) : FeedAction
}

sealed interface FeedEvent {
    data class Error(val error: UiText) : FeedEvent
    data class NavigateToProfile(val userId: String) : FeedEvent
}
