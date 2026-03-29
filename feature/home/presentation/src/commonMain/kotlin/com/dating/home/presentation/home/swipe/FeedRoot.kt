package com.dating.home.presentation.home.swipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoot(
    onNavigateToProfile: (String, String?) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    swipedUserId: String? = null,
    swipedIsDislike: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(swipedUserId) {
        if (swipedUserId != null) {
            viewModel.onAction(FeedAction.OnUserSwiped(swipedUserId, swipedIsDislike))
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(FeedAction.OnScreenResumed)
        onPauseOrDispose {}
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FeedEvent.NavigateToProfile -> {
                onNavigateToProfile(event.userId, event.imageUrl)
            }
            is FeedEvent.NavigateToEditProfile -> {
                onNavigateToEditProfile()
            }
        }
    }

    FeedScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
