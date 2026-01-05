package com.dating.chat.presentation.manage_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.chat.presentation.generated.resources.Res
import aura.feature.chat.presentation.generated.resources.chat_members
import aura.feature.chat.presentation.generated.resources.save
import com.dating.chat.presentation.components.manage_chat.ManageChatAction
import com.dating.chat.presentation.components.manage_chat.ManageChatScreen
import com.dating.core.designsystem.components.dialogs.ChirpAdaptiveDialogSheetLayout
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageChatRoot(
    chatId: String?,
    onDismiss: () -> Unit,
    onMembersAdded: () -> Unit,
    viewModel: ManageChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(chatId) {
        viewModel.onAction(ManageChatAction.ChatParticipants.OnSelectChat(chatId))
    }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is ManageChatEvent.OnMembersAdded -> onMembersAdded()
        }
    }

    ChirpAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ManageChatScreen(
            headerText = stringResource(Res.string.chat_members),
            primaryButtonText = stringResource(Res.string.save),
            state = state,
            onAction = { action ->
                when(action) {
                    ManageChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}
