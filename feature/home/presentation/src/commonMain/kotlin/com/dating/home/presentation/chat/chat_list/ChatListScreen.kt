@file:OptIn(ExperimentalMaterial3Api::class)

package com.dating.home.presentation.chat.chat_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete_chat
import aura.feature.home.presentation.generated.resources.delete_chat_desc
import aura.feature.home.presentation.generated.resources.delete_chat_title
import aura.feature.home.presentation.generated.resources.no_chats
import aura.feature.home.presentation.generated.resources.no_chats_subtitle
import aura.feature.home.presentation.generated.resources.search_conversations
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.MainTopAppBar
import com.dating.core.designsystem.components.textfields.ChirpMultiLineTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.permissions.Permission
import com.dating.core.presentation.permissions.rememberPermissionController
import com.dating.home.presentation.chat.chat_list.components.ChatListItemSkeleton
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainTopAppBar(
                title = "Chats",
                actions = {
                    IconButton(
                        onClick = { onAction(ChatListAction.OnToggleSearch) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search_conversations),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )

            AnimatedVisibility(
                visible = state.isSearchVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                ChirpMultiLineTextField(
                    state = state.searchTextFieldState,
                    placeholder = stringResource(Res.string.search_conversations),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { onAction(ChatListAction.OnRefresh) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    state.isLoading && state.chats.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(6) {
                                ChatListItemSkeleton(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    state.filteredChats.isEmpty() && !state.isLoading -> {
                        EmptySection(
                            title = stringResource(Res.string.no_chats),
                            description = stringResource(Res.string.no_chats_subtitle),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(
                                items = state.filteredChats,
                                key = { it.id }
                            ) { chatUi ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            onAction(ChatListAction.OnSwipeToDeleteChat(chatUi.id))
                                            false
                                        } else false
                                    }
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val color by animateColorAsState(
                                            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                                MaterialTheme.colorScheme.error
                                            } else {
                                                MaterialTheme.colorScheme.surface
                                            },
                                            label = "swipe_bg"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(color)
                                                .padding(horizontal = 20.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(Res.string.delete_chat),
                                                tint = MaterialTheme.colorScheme.onError,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    enableDismissFromStartToEnd = false,
                                    modifier = Modifier.animateItem()
                                ) {
                                    ChatListItemUi(
                                        chat = chatUi,
                                        isSelected = chatUi.id == state.selectedChatId,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onAction(ChatListAction.OnSelectChat(chatUi.id))
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showDeleteConfirmationForChatId != null) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_chat_title),
            description = stringResource(Res.string.delete_chat_desc),
            confirmButtonText = stringResource(Res.string.delete_chat),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { onAction(ChatListAction.OnConfirmDeleteChat) },
            onCancelClick = { onAction(ChatListAction.OnDismissDeleteChatDialog) },
            onDismiss = { onAction(ChatListAction.OnDismissDeleteChatDialog) }
        )
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
