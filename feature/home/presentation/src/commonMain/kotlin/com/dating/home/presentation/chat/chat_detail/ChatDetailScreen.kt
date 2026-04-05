@file:OptIn(ExperimentalUuidApi::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.dating.home.presentation.chat.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.no_chat_selected
import aura.feature.home.presentation.generated.resources.select_a_chat
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import aura.feature.home.presentation.generated.resources.block_user
import aura.feature.home.presentation.generated.resources.block_user_title
import aura.feature.home.presentation.generated.resources.block_user_desc
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete_match
import aura.feature.home.presentation.generated.resources.delete_match_title
import aura.feature.home.presentation.generated.resources.delete_match_desc
import aura.feature.home.presentation.generated.resources.block_after_report_confirm
import aura.feature.home.presentation.generated.resources.block_after_report_desc
import aura.feature.home.presentation.generated.resources.block_after_report_dismiss
import aura.feature.home.presentation.generated.resources.block_after_report_title
import aura.feature.home.presentation.generated.resources.report_success
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.home.presentation.report.ReportUserBottomSheet
import com.dating.home.presentation.chat.chat_detail.components.ChatDetailHeader
import com.dating.home.presentation.chat.chat_detail.components.DateChip
import com.dating.home.presentation.chat.chat_detail.components.MessageBannerListener
import com.dating.home.presentation.chat.chat_detail.components.MediaPickerContent
import com.dating.home.presentation.chat.chat_detail.components.MediaPickerOption
import com.dating.home.presentation.chat.chat_detail.components.DateProposalSheet
import com.dating.home.presentation.chat.chat_detail.components.MessageBox
import com.dating.home.presentation.chat.chat_detail.components.MessageList
import com.dating.home.presentation.chat.chat_detail.components.PaginationScrollListener
import com.dating.home.presentation.chat.chat_detail.components.TypingIndicator
import com.dating.core.designsystem.components.brand.ChirpHorizontalDivider
import com.dating.core.designsystem.components.header.TopAppBarGeneric
import com.dating.home.domain.models.MessageType
import com.dating.home.presentation.chat.components.EmptySection
import com.dating.home.presentation.chat.model.ChatUi
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.core.presentation.util.currentDeviceConfiguration
import com.dating.home.presentation.chat.audiorecorder.AudioRecorderSheet
import com.dating.home.presentation.chat.chat_detail.components.EmojiPickerOverlay
import com.dating.home.presentation.chat.gif.GifPickerSheet
import com.dating.home.presentation.chat.mediapicker.MediaFilter
import com.dating.home.presentation.chat.mediapicker.rememberMediaPickerLauncher
import com.dating.home.domain.giphy.GiphyService
import org.koin.compose.koinInject
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatDetailRoot(
    chatId: String?,
    onBack: () -> Unit,
    onProfileClick: (userId: String) -> Unit,
    onForceLogout: () -> Unit = {},
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarState = remember { SnackbarHostState() }
    val messageListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var showMediaPickerSheet by remember { mutableStateOf(false) }
    var showAudioRecorderSheet by remember { mutableStateOf(false) }
    var showGifPickerSheet by remember { mutableStateOf(false) }
    var emojiPickerTargetMessageId by remember { mutableStateOf<String?>(null) }
    val giphyService: GiphyService = koinInject()

    val onMediaPicked: (com.dating.home.presentation.profile.mediapicker.PickedImageData) -> Unit = { pickedData ->
        val mimeType = pickedData.mimeType ?: "image/jpeg"
        val messageType = when {
            mimeType == "image/gif" -> MessageType.GIF
            mimeType.startsWith("image/") -> MessageType.IMAGE
            mimeType.startsWith("audio/") -> MessageType.AUDIO
            else -> MessageType.IMAGE
        }
        viewModel.onAction(
            ChatDetailAction.OnSendMediaMessage(
                mediaBytes = pickedData.bytes,
                mimeType = mimeType,
                messageType = messageType
            )
        )
    }

    val imagePickerLauncher = rememberMediaPickerLauncher(
        mediaFilter = MediaFilter.IMAGES_AND_GIFS,
        onResult = onMediaPicked
    )

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatDetailEvent.OnChatLeft -> onBack()
            ChatDetailEvent.OnUserBlocked -> onBack()
            ChatDetailEvent.OnMatchDeleted -> onBack()
            ChatDetailEvent.OnNewMessage -> {
                scope.launch {
                    messageListState.animateScrollToItem(0)
                }
            }

            is ChatDetailEvent.OnError -> {
                snackbarState.showSnackbar(event.error.asStringAsync())
            }
            ChatDetailEvent.OnReportSuccess -> {
                snackbarState.showSnackbar(
                    org.jetbrains.compose.resources.getString(Res.string.report_success)
                )
            }
            is ChatDetailEvent.OnNavigateToProfile -> onProfileClick(event.userId)
            ChatDetailEvent.OnForceLogout -> onForceLogout()
        }
    }

    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    LaunchedEffect(chatId) {
        if (chatId != null) {
            messageListState.scrollToItem(0)
        }
    }

    BackHandler(
        enabled = true
    ) {
        scope.launch {
            // Add artificial delay to prevent detail back animation from showing
            // an unselected chat the moment we go back
            delay(300)
            viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        }
        onBack()
    }

    // Navigate to search result when index changes
    LaunchedEffect(state.currentSearchResultIndex, state.messageSearchResults) {
        if (state.currentSearchResultIndex >= 0 && state.messageSearchResults.isNotEmpty()) {
            val messageIndex = state.messageSearchResults[state.currentSearchResultIndex]
            messageListState.animateScrollToItem(messageIndex)
        }
    }

    Box {
        ChatDetailScreen(
            state = state,
            messageListState = messageListState,
            onAction = { action ->
                when (action) {
                    is ChatDetailAction.OnBackClick -> onBack()
                    is ChatDetailAction.OnCopyMessage -> {
                        clipboardManager.setText(AnnotatedString(action.content))
                    }
                    is ChatDetailAction.OnMessageLongClick -> {
                        emojiPickerTargetMessageId = action.message.id
                    }
                    else -> Unit
                }
                viewModel.onAction(action)
            },
            onAttachClick = { showMediaPickerSheet = true },
            onProposeDateClick = { viewModel.onAction(ChatDetailAction.OnProposeDateClick) },
            snackbarState = snackbarState
        )

        EmojiPickerOverlay(
            visible = emojiPickerTargetMessageId != null,
            onEmojiSelected = { emoji ->
                emojiPickerTargetMessageId?.let { messageId ->
                    viewModel.onAction(ChatDetailAction.OnReactToMessage(messageId, emoji))
                }
                emojiPickerTargetMessageId = null
            },
            onDismiss = {
                emojiPickerTargetMessageId = null
            }
        )
    }

    if (showMediaPickerSheet) {
        val mediaSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showMediaPickerSheet = false },
            sheetState = mediaSheetState
        ) {
            MediaPickerContent(
                onOptionSelected = { option ->
                    scope.launch {
                        mediaSheetState.hide()
                        showMediaPickerSheet = false
                        when (option) {
                            MediaPickerOption.GALLERY -> imagePickerLauncher.launch()
                            MediaPickerOption.GIF -> showGifPickerSheet = true
                            MediaPickerOption.AUDIO -> showAudioRecorderSheet = true
                        }
                    }
                }
            )
        }
    }

    if (showAudioRecorderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAudioRecorderSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            AudioRecorderSheet(
                onDismiss = { showAudioRecorderSheet = false },
                onRecordingComplete = { pickedData ->
                    showAudioRecorderSheet = false
                    onMediaPicked(pickedData)
                }
            )
        }
    }

    if (showGifPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGifPickerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            GifPickerSheet(
                giphyService = giphyService,
                onGifSelected = { gif ->
                    showGifPickerSheet = false
                    viewModel.onAction(ChatDetailAction.OnSendGifUrl(gif.originalUrl))
                }
            )
        }
    }

    if (state.showDateProposalSheet) {
        DateProposalSheet(
            onDismiss = { viewModel.onAction(ChatDetailAction.OnDismissDateProposalSheet) },
            onSubmit = { dateTime, location ->
                viewModel.onAction(ChatDetailAction.OnSubmitDateProposal(dateTime, location))
            },
            initialDateTime = state.editingProposalDateTime,
            initialLocation = state.editingProposalLocation,
            isEditing = state.editingProposalMessageId != null
        )
    }

    if (state.showBlockDialog) {
        val username = state.chatUi?.otherParticipants?.firstOrNull()?.username ?: ""
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.block_user_title, username),
            description = stringResource(Res.string.block_user_desc),
            confirmButtonText = stringResource(Res.string.block_user),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(ChatDetailAction.OnConfirmBlockUser) },
            onCancelClick = { viewModel.onAction(ChatDetailAction.OnDismissBlockDialog) },
            onDismiss = { viewModel.onAction(ChatDetailAction.OnDismissBlockDialog) }
        )
    }

    if (state.showDeleteMatchDialog) {
        val username = state.chatUi?.otherParticipants?.firstOrNull()?.username ?: ""
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_match_title, username),
            description = stringResource(Res.string.delete_match_desc),
            confirmButtonText = stringResource(Res.string.delete_match),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(ChatDetailAction.OnConfirmDeleteMatch) },
            onCancelClick = { viewModel.onAction(ChatDetailAction.OnDismissDeleteMatchDialog) },
            onDismiss = { viewModel.onAction(ChatDetailAction.OnDismissDeleteMatchDialog) }
        )
    }

    if (state.showBlockAfterReportDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.block_after_report_title),
            description = stringResource(Res.string.block_after_report_desc),
            confirmButtonText = stringResource(Res.string.block_after_report_confirm),
            cancelButtonText = stringResource(Res.string.block_after_report_dismiss),
            onConfirmClick = { viewModel.onAction(ChatDetailAction.OnConfirmBlockAfterReport) },
            onCancelClick = { viewModel.onAction(ChatDetailAction.OnDismissBlockAfterReportDialog) },
            onDismiss = { viewModel.onAction(ChatDetailAction.OnDismissBlockAfterReportDialog) }
        )
    }

    if (state.showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(ChatDetailAction.OnDismissReportSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ReportUserBottomSheet(
                isSubmitting = state.isSubmittingReport,
                onSubmit = { reason, description ->
                    viewModel.onAction(ChatDetailAction.OnSubmitReport(reason, description))
                }
            )
        }
    }
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    messageListState: LazyListState,
    snackbarState: SnackbarHostState,
    onAction: (ChatDetailAction) -> Unit,
    onAttachClick: () -> Unit = {},
    onProposeDateClick: () -> Unit = {},
) {
    val configuration = currentDeviceConfiguration()

    val realMessageItemCount = remember(state.messages) {
        state
            .messages
            .filter { it is MessageUi.LocalUserMessage || it is MessageUi.OtherUserMessage }
            .size
    }

    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleIndex, totalItemsCount) ->
            firstVisibleIndex >= 0 && totalItemsCount > 0
        }.collect { (firstVisibleItemIndex, _) ->
            onAction(ChatDetailAction.OnFirstVisibleIndexChanged(firstVisibleItemIndex))
        }
    }

    MessageBannerListener(
        lazyListState = messageListState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = { index ->
            onAction(ChatDetailAction.OnTopVisibleIndexChanged(index))
        },
        onHide = {
            onAction(ChatDetailAction.OnHideBanner)
        }
    )

    PaginationScrollListener(
        lazyListState = messageListState,
        itemCount = realMessageItemCount,
        isPaginationLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = {
            onAction(ChatDetailAction.OnScrollToTop)
        }
    )

    var headerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clearFocusOnTap()
                .then(
                    if (configuration.isWideScreen) {
                        Modifier.padding(horizontal = 8.dp)
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicRoundedCornerColumn(
                    isCornersRounded = configuration.isWideScreen,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (state.chatUi == null) {
                        EmptySection(
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    } else {
                        TopAppBarGeneric(
                            divider = true,
                            modifier = Modifier
                                .onSizeChanged {
                                    headerHeight = with(density) {
                                        it.height.toDp()
                                    }
                                }
                        ) {
                            ChatDetailHeader(
                                chatUi = state.chatUi,
                                isChatOptionsDropDownOpen = state.isChatOptionsOpen,
                                isSearchMode = state.isSearchMode,
                                messageSearchQuery = state.messageSearchQuery,
                                searchResultCount = state.messageSearchResults.size,
                                currentSearchResultIndex = state.currentSearchResultIndex,
                                onChatOptionsClick = {
                                    onAction(ChatDetailAction.OnChatOptionsClick)
                                },
                                onDismissChatOptions = {
                                    onAction(ChatDetailAction.OnDismissChatOptions)
                                },
                                onProfileClick = { userId ->
                                    onAction(ChatDetailAction.OnProfileClick(userId))
                                },
                                onLeaveChatClick = {
                                    onAction(ChatDetailAction.OnLeaveChatClick)
                                },
                                onBlockUserClick = {
                                    onAction(ChatDetailAction.OnBlockUserClick)
                                },
                                onDeleteMatchClick = {
                                    onAction(ChatDetailAction.OnDeleteMatchClick)
                                },
                                onReportUserClick = {
                                    onAction(ChatDetailAction.OnReportUserClick)
                                },
                                onBackClick = {
                                    onAction(ChatDetailAction.OnBackClick)
                                },
                                onToggleSearch = {
                                    onAction(ChatDetailAction.OnToggleMessageSearch)
                                },
                                onSearchQueryChanged = { query ->
                                    onAction(ChatDetailAction.OnMessageSearchQueryChanged(query))
                                },
                                onNextSearchResult = {
                                    onAction(ChatDetailAction.OnNextSearchResult)
                                },
                                onPreviousSearchResult = {
                                    onAction(ChatDetailAction.OnPreviousSearchResult)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        MessageList(
                            messages = state.messages,
                            messageWithOpenMenu = state.messageWithOpenMenu,
                            listState = messageListState,
                            isPaginationLoading = state.isPaginationLoading,
                            paginationError = state.paginationError?.asString(),
                            highlightText = if (state.isSearchMode) state.messageSearchQuery else null,
                            onMessageLongClick = { message ->
                                onAction(ChatDetailAction.OnMessageLongClick(message))
                            },
                            onMessageRetryClick = { message ->
                                onAction(ChatDetailAction.OnRetryClick(message))
                            },
                            onDismissMessageMenu = {
                                onAction(ChatDetailAction.OnDismissMessageMenu)
                            },
                            onDeleteMessageClick = { message ->
                                onAction(ChatDetailAction.OnDeleteMessageClick(message))
                            },
                            onRetryPaginationClick = {
                                onAction(ChatDetailAction.OnRetryPaginationClick)
                            },
                            onCopyClick = { content ->
                                onAction(ChatDetailAction.OnCopyMessage(content))
                            },
                            onReactionTapped = { messageId, emoji ->
                                onAction(ChatDetailAction.OnReactToMessage(messageId, emoji))
                            },
                            onDoubleTapReact = { messageId ->
                                onAction(ChatDetailAction.OnReactToMessage(messageId, "❤️"))
                            },
                            onAcceptProposal = { messageId ->
                                onAction(ChatDetailAction.OnAcceptProposal(messageId))
                            },
                            onRejectProposal = { messageId ->
                                onAction(ChatDetailAction.OnRejectProposal(messageId))
                            },
                            onCancelProposal = { messageId ->
                                onAction(ChatDetailAction.OnCancelProposal(messageId))
                            },
                            onEditProposal = { messageId, dateTime, location ->
                                onAction(ChatDetailAction.OnEditProposal(messageId, dateTime, location))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        TypingIndicator(
                            typingUsernames = state.typingUsernames,
                            modifier = Modifier.fillMaxWidth()
                        )

                        AnimatedVisibility(
                            visible = !configuration.isWideScreen
                        ) {
                            Column {
                                ChirpHorizontalDivider()
                                MessageBox(
                                    messageTextFieldState = state.messageTextFieldState,
                                    isSendButtonEnabled = state.canSendMessage,
                                    connectionState = state.connectionState,
                                    onSendClick = {
                                        onAction(ChatDetailAction.OnSendMessageClick)
                                    },
                                    onTextChanged = { text ->
                                        onAction(ChatDetailAction.OnTextChanged(text))
                                    },
                                    onAttachClick = onAttachClick,
                                    onProposeDateClick = onProposeDateClick,
                                    isUploadingMedia = state.isUploadingMedia,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .imePadding()
                                        .padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp
                                        )
                                )
                            }
                        }
                    }
                }

                if (configuration.isWideScreen) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                AnimatedVisibility(
                    visible = configuration.isWideScreen && state.chatUi != null
                ) {
                    DynamicRoundedCornerColumn(
                        isCornersRounded = configuration.isWideScreen
                    ) {
                        TypingIndicator(
                            typingUsernames = state.typingUsernames,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ChirpHorizontalDivider()
                        MessageBox(
                            messageTextFieldState = state.messageTextFieldState,
                            isSendButtonEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendClick = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            onTextChanged = { text ->
                                onAction(ChatDetailAction.OnTextChanged(text))
                            },
                            onAttachClick = onAttachClick,
                            onProposeDateClick = onProposeDateClick,
                            isUploadingMedia = state.isUploadingMedia,
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                                .padding(8.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = headerHeight + 16.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString()
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicRoundedCornerColumn(
    isCornersRounded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = if (isCornersRounded) 8.dp else 0.dp,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape,
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape
            )
    ) {
        content()
    }
}

@Preview
@Composable
private fun ChatDetailEmptyPreview() {
    AppTheme {
        ChatDetailScreen(
            state = ChatDetailState(),
            onAction = {},
            messageListState = rememberLazyListState(),
            snackbarState = remember { SnackbarHostState() }
        )
    }
}

@Preview
@Composable
private fun ChatDetailMessagesPreview() {
    AppTheme(darkTheme = true) {
        ChatDetailScreen(
            messageListState = rememberLazyListState(),
            state = ChatDetailState(
                messageTextFieldState = rememberTextFieldState(
                    initialText = "This is a new message!"
                ),
                canSendMessage = true,
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
                messages = (1..20).map {
                    if (it % 2 == 0) {
                        MessageUi.LocalUserMessage(
                            id = Uuid.random().toString(),
                            content = "Hello world!",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT,
                            formattedSentTime = UiText.DynamicString("Friday, Aug 20")
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = Uuid.random().toString(),
                            content = "Hello world!",
                            sender = ChatParticipantUi(
                                id = Uuid.random().toString(),
                                username = "John",
                                initials = "JO"
                            ),
                            formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                        )
                    }
                }
            ),
            onAction = {},
            snackbarState = remember { SnackbarHostState() }
        )
    }
}
