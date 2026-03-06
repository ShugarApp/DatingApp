package com.dating.home.presentation.matches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesRoot(
    onNavigateToProfile: (String, String?) -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MatchesEvent.Error -> { /* Handle error */ }
            is MatchesEvent.NavigateToProfile -> {
                onNavigateToProfile(event.userId, event.imageUrl)
            }
            is MatchesEvent.NavigateToChat -> {
                onNavigateToChatDetail(event.chatId)
            }
        }
    }

    MatchesScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
