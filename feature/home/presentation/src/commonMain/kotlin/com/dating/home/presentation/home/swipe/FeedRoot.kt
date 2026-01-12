package com.dating.home.presentation.home.swipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoot(
    onNavigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is FeedEvent.Error -> {
                // Handle error
            }

            is FeedEvent.NavigateToProfile -> {
                onNavigateToProfile(event.userId)
            }
        }
    }

    FeedScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
