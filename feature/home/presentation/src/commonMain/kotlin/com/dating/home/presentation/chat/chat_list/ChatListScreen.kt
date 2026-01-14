package com.dating.home.presentation.chat.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.create_chat
import aura.feature.home.presentation.generated.resources.no_chats
import aura.feature.home.presentation.generated.resources.no_chats_subtitle
import com.dating.core.designsystem.components.brand.ChirpHorizontalDivider
import com.dating.core.designsystem.components.buttons.ChirpFloatingActionButton
import com.dating.core.designsystem.components.header.MainTopAppBar
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.permissions.Permission
import com.dating.core.presentation.permissions.rememberPermissionController
import com.dating.home.presentation.chat.chat_list.components.ChatListItemUi
import com.dating.home.presentation.chat.components.EmptySection
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListRoot(
    selectedChatId: String?,
    onChatClick: (String?) -> Unit,
    onCreateChatClick: () -> Unit,
    viewModel: ChatListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedChatId) {
        viewModel.onAction(ChatListAction.OnSelectChat(selectedChatId))
    }

    ChatListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ChatListAction.OnSelectChat -> onChatClick(action.chatId)
                ChatListAction.OnCreateChatClick -> onCreateChatClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val permissionController = rememberPermissionController()
    LaunchedEffect(true) {
        permissionController.requestPermission(Permission.NOTIFICATIONS)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ChirpFloatingActionButton(
                onClick = {
                    onAction(ChatListAction.OnCreateChatClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.create_chat)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainTopAppBar(title = "Chats")
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.chats.isEmpty() -> {
                    EmptySection(
                        title = stringResource(Res.string.no_chats),
                        description = stringResource(Res.string.no_chats_subtitle),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = state.chats,
                            key = { it.id }
                        ) { chatUi ->
                            ChatListItemUi(
                                chat = chatUi,
                                isSelected = chatUi.id == state.selectedChatId,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAction(ChatListAction.OnSelectChat(chatUi.id))
                                    }
                            )
                            ChirpHorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        ChatListScreen(
            state = ChatListState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}