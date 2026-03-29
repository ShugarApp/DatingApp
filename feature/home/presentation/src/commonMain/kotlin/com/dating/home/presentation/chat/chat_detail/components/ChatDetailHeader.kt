package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import shugar.core.designsystem.generated.resources.Res as DesignSystemRes
import shugar.core.designsystem.generated.resources.arrow_left_icon
import shugar.core.designsystem.generated.resources.dots_icon
import shugar.core.designsystem.generated.resources.log_out_icon
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.block_user
import shugar.feature.home.presentation.generated.resources.chat_members
import shugar.feature.home.presentation.generated.resources.delete_match
import shugar.feature.home.presentation.generated.resources.go_back
import shugar.feature.home.presentation.generated.resources.leave_chat
import shugar.feature.home.presentation.generated.resources.open_chat_options_menu
import shugar.feature.home.presentation.generated.resources.report_user
import shugar.feature.home.presentation.generated.resources.search_messages
import shugar.feature.home.presentation.generated.resources.users_icon
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.presentation.chat.components.ChatItemHeaderRow
import com.dating.home.presentation.chat.model.ChatUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.designsystem.components.buttons.ChirpIconButton
import com.dating.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.dating.core.designsystem.components.dropdown.DropDownItem
import com.dating.core.designsystem.components.header.TopAppBarGeneric
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi?,
    isChatOptionsDropDownOpen: Boolean,
    isSearchMode: Boolean,
    messageSearchQuery: String,
    searchResultCount: Int,
    currentSearchResultIndex: Int,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: () -> Unit,
    onProfileClick: (userId: String) -> Unit,
    onLeaveChatClick: () -> Unit,
    onBlockUserClick: () -> Unit,
    onDeleteMatchClick: () -> Unit,
    onReportUserClick: () -> Unit,
    onBackClick: () -> Unit,
    onToggleSearch: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onNextSearchResult: () -> Unit,
    onPreviousSearchResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isSearchMode,
        modifier = modifier
    ) { searchMode ->
        if (searchMode) {
            SearchBar(
                query = messageSearchQuery,
                resultCount = searchResultCount,
                currentResultIndex = currentSearchResultIndex,
                onQueryChanged = onSearchQueryChanged,
                onClose = onToggleSearch,
                onNext = onNextSearchResult,
                onPrevious = onPreviousSearchResult,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChirpIconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                        contentDescription = stringResource(Res.string.go_back),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (chatUi != null) {
                    val isGroupChat = chatUi.otherParticipants.size > 1
                    val otherUserId = chatUi.otherParticipants.firstOrNull()?.id
                    ChatItemHeaderRow(
                        chat = chatUi,
                        isGroupChat = isGroupChat,
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (otherUserId != null) {
                                    Modifier.clickable { onProfileClick(otherUserId) }
                                } else Modifier
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(onClick = onToggleSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(Res.string.search_messages),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box {
                    ChirpIconButton(
                        onClick = onChatOptionsClick
                    ) {
                        Icon(
                            imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                            contentDescription = stringResource(Res.string.open_chat_options_menu),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    ChirpDropDownMenu(
                        isOpen = isChatOptionsDropDownOpen,
                        onDismiss = onDismissChatOptions,
                        items = listOf(
                            DropDownItem(
                                title = stringResource(Res.string.delete_match),
                                icon = Icons.Default.HeartBroken,
                                contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                                onClick = onDeleteMatchClick
                            ),
                            DropDownItem(
                                title = stringResource(Res.string.report_user),
                                icon = Icons.Default.Flag,
                                contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                                onClick = onReportUserClick
                            ),
                            DropDownItem(
                                title = stringResource(Res.string.block_user),
                                icon = Icons.Default.Block,
                                contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                                onClick = onBlockUserClick
                            ),
                            DropDownItem(
                                title = stringResource(Res.string.leave_chat),
                                icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                                contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                                onClick = onLeaveChatClick
                            ),
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    resultCount: Int,
    currentResultIndex: Int,
    onQueryChanged: (String) -> Unit,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.go_back),
                modifier = Modifier.size(20.dp)
            )
        }

        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onNext() }),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_messages),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (query.isNotEmpty()) {
            Text(
                text = if (resultCount > 0) "${currentResultIndex + 1}/$resultCount" else "0/0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onPrevious, enabled = resultCount > 0) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onNext, enabled = resultCount > 0) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun ChatDetailHeaderPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBarGeneric {
                ChatDetailHeader(
                    isChatOptionsDropDownOpen = true,
                    isSearchMode = false,
                    messageSearchQuery = "",
                    searchResultCount = 0,
                    currentSearchResultIndex = -1,
                    chatUi = ChatUi(
                        id = "1",
                        localParticipant = ChatParticipantUi(
                            id = "1",
                            username = "Philipp",
                            initials = "PH",
                        ),
                        otherParticipants = listOf(
                            ChatParticipantUi(
                                id = "2",
                                username = "Cinderella",
                                initials = "CI",
                            ),
                            ChatParticipantUi(
                                id = "3",
                                username = "Josh",
                                initials = "JO",
                            )
                        ),
                        lastMessage = ChatMessage(
                            id = "1",
                            chatId = "1",
                            content = "This is a last chat message that was sent by Philipp " +
                                "and goes over multiple lines to showcase the ellipsis",
                            createdAt = Clock.System.now(),
                            senderId = "1",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT
                        ),
                        lastMessageSenderUsername = "Philipp"
                    ),
                    onChatOptionsClick = {},
                    onProfileClick = {},
                    onLeaveChatClick = {},
                    onBlockUserClick = {},
                    onDeleteMatchClick = {},
                    onReportUserClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                    onToggleSearch = {},
                    onSearchQueryChanged = {},
                    onNextSearchResult = {},
                    onPreviousSearchResult = {},
                )
            }
        }
    }
}
