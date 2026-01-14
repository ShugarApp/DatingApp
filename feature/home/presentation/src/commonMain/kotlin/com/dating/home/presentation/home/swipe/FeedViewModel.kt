package com.dating.home.presentation.home.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

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
            is FeedAction.OnLikePost -> removeItem(action.postId) // Treat like as removing card
            is FeedAction.OnPass -> removeItem(action.postId) // Treat pass as removing card
            is FeedAction.OnUserClick -> {
                viewModelScope.launch {
                    _events.send(FeedEvent.NavigateToProfile(action.userId, action.imageUrl))
                }
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Simulating feed data
            val mockFeedItems = listOf(
                FeedItem(
                    id = "1",
                    userId = "user1",
                    userName = "María García",
                    userImageUrl = null,
                    content = "¡Hermoso día para salir a caminar! 🌞",
                    imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    timestamp = 1716920000000L,
                    likesCount = 12,
                    isLiked = false
                ),
                FeedItem(
                    id = "2",
                    userId = "user2",
                    userName = "Carlos Rodríguez",
                    userImageUrl = null,
                    content = "Disfrutando de un café ☕",
                    imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    timestamp = 1716920000000L,
                    likesCount = 8,
                    isLiked = true
                ),
                FeedItem(
                    id = "3",
                    userId = "user3",
                    userName = "Ana Martinez",
                    userImageUrl = null,
                    content = "La naturaleza es increíble 🌲",
                    imageUrl = "https://images.unsplash.com/photo-1554151228-14d9def656ec",
                    timestamp = 1716920000000L,
                    likesCount = 20,
                    isLiked = false
                )
            )
            
            _state.update {
                it.copy(
                    isLoading = false,
                    feedItems = mockFeedItems
                )
            }
        }
    }

    private fun removeItem(postId: String) {
        _state.update { currentState ->
            val updatedItems = currentState.feedItems.filter { it.id != postId }
            currentState.copy(feedItems = updatedItems)
        }
    }
}
