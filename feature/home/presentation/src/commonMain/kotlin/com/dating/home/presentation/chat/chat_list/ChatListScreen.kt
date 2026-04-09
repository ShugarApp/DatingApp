@file:OptIn(ExperimentalMaterial3Api::class)

package com.dating.home.presentation.chat.chat_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
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
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
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
            AnimatedContent(
                targetState = state.isSearchVisible,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "search_header"
            ) { isSearching ->
                if (isSearching) {
                    SearchTopBar(
                        searchState = state.searchTextFieldState,
                        placeholder = stringResource(Res.string.search_conversations),
                        onCloseSearch = { onAction(ChatListAction.OnToggleSearch) }
                    )
                } else {
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
                }
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

@Composable
private fun SearchTopBar(
    searchState: TextFieldState,
    placeholder: String,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = {
            focusManager.clearFocus()
            onCloseSearch()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Close search",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.extended.surfaceLower,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.textPlaceholder,
                modifier = Modifier.size(18.dp)
            )

            BasicTextField(
                state = searchState,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.extended.textPrimary
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorator = { innerBox ->
                    if (searchState.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.extended.textPlaceholder
                        )
                    }
                    innerBox()
                }
            )

            AnimatedVisibility(
                visible = searchState.text.isNotEmpty(),
                enter = fadeIn(tween(150)) + scaleIn(tween(150)),
                exit = fadeOut(tween(100)) + scaleOut(tween(100))
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.extended.textPlaceholder.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            searchState.edit { delete(0, length) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.extended.textPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Right padding balance
        Box(modifier = Modifier.size(4.dp))
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
