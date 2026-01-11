package com.dating.chat.presentation.matches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesRoot(
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MatchesScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
