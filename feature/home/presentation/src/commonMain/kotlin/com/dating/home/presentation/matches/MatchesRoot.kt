package com.dating.home.presentation.matches

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete_match
import aura.feature.home.presentation.generated.resources.delete_match_desc
import aura.feature.home.presentation.generated.resources.delete_match_success
import aura.feature.home.presentation.generated.resources.delete_match_title
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesRoot(
    onNavigateToProfile: (String, String?) -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MatchesEvent.Error -> { /* Handle error */ }
            is MatchesEvent.NavigateToProfile -> {
                onNavigateToProfile(event.userId, event.imageUrl)
            }
            is MatchesEvent.NavigateToChat -> {
                onNavigateToChatDetail(event.chatId)
            }
            MatchesEvent.MatchDeleted -> {
                snackbarState.showSnackbar(
                    message = "Match deleted"
                )
            }
        }
    }

    MatchesScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )

    if (state.showDeleteMatchDialog) {
        val username = state.matchToDelete?.username ?: ""
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_match_title, username),
            description = stringResource(Res.string.delete_match_desc),
            confirmButtonText = stringResource(Res.string.delete_match),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(MatchesAction.OnConfirmDeleteMatch) },
            onCancelClick = { viewModel.onAction(MatchesAction.OnDismissDeleteMatchDialog) },
            onDismiss = { viewModel.onAction(MatchesAction.OnDismissDeleteMatchDialog) }
        )
    }
}
