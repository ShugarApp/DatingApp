@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package com.dating.home.presentation.chat.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.report_duplicate
import shugar.feature.home.presentation.generated.resources.today
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.DataErrorException
import com.dating.core.domain.util.Paginator
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import com.dating.core.domain.util.DataError
import com.dating.home.domain.block.BlockService
import com.dating.home.domain.chat.ChatConnectionClient
import com.dating.home.domain.chat.ChatRepository
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.message.MessageRepository
import com.dating.home.domain.report.ReportReason
import com.dating.home.domain.report.ReportService
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.ConnectionState
import com.dating.home.domain.models.OutgoingNewMessage
import com.dating.home.presentation.chat.mappers.toUi
import com.dating.home.presentation.chat.mappers.toUiList
import com.dating.home.presentation.chat.model.MessageUi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val messageRepository: MessageRepository,
    private val connectionClient: ChatConnectionClient,
    private val blockService: BlockService,
    private val matchingService: MatchingService,
    private val reportService: ReportService
) : ViewModel() {

    private val eventChannel = Channel<ChatDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _chatId = MutableStateFlow<String?>(null)

    private var hasLoadedInitialData = false

    private var currentPaginator: Paginator<String?, ChatMessage>? = null

    private var lastTypingSentMs: Long = 0L
    private val typingUsers = MutableStateFlow<Map<String, String>>(emptyMap()) // userId -> username
    private val typingTimeoutJobs = mutableMapOf<String, Job>()

    private val chatInfoFlow = _chatId
        .onEach { chatId ->
            if (chatId != null) {
                setupPaginatorForChat(chatId)
                loadNextItems()
            } else {
                currentPaginator = null
            }
        }
        .flatMapLatest { chatId ->
            if (chatId != null) {
                chatRepository.getChatInfoById(chatId)
            } else emptyFlow()
        }

    private val _state = MutableStateFlow(ChatDetailState())

    private val canSendMessage = snapshotFlow { _state.value.messageTextFieldState.text.toString() }
        .map { it.isBlank() }
        .combine(connectionClient.connectionState) { isMessageBlank, connectionState ->
            !isMessageBlank && connectionState == ConnectionState.CONNECTED
        }


    private val stateWithMessages = combine(
        _state,
        chatInfoFlow,
        sessionStorage.observeAuthInfo(),
        typingUsers
    ) { currentState, chatInfo, authInfo, typing ->
        if (authInfo == null) {
            return@combine ChatDetailState()
        }

        currentState.copy(
            chatUi = chatInfo.chat.toUi(authInfo.user.id),
            messages = chatInfo.messages.toUiList(authInfo.user.id),
            typingUsernames = typing.values.toList()
        )
    }

    val state = _chatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                stateWithMessages
            } else {
                _state
            }
        }
        .onStart {
            if (!hasLoadedInitialData) {
                observeConnectionState()
                observeChatMessages()
                observeCanSendMessage()
                observeTypingIndicators()
                observeMessagesRead()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatDetailState()
        )

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> switchChat(action.chatId)
            ChatDetailAction.OnChatOptionsClick -> onChatOptionsClick()
            is ChatDetailAction.OnDeleteMessageClick -> deleteMessage(action.message)
            ChatDetailAction.OnDismissChatOptions -> onDismissChatOptions()
            ChatDetailAction.OnDismissMessageMenu -> onDismissMessageMenu()
            ChatDetailAction.OnLeaveChatClick -> onLeaveChatClick()
            is ChatDetailAction.OnMessageLongClick -> onMessageLongClick(action.message)
            is ChatDetailAction.OnRetryClick -> retryMessage(action.message)
            ChatDetailAction.OnScrollToTop -> onScrollToTop()
            ChatDetailAction.OnSendMessageClick -> sendMessage()
            ChatDetailAction.OnRetryPaginationClick -> retryPagination()
            ChatDetailAction.OnHideBanner -> hideBanner()
            is ChatDetailAction.OnTopVisibleIndexChanged -> updateBanner(action.topVisibleIndex)
            is ChatDetailAction.OnFirstVisibleIndexChanged -> updateNearBottom(action.index)
            is ChatDetailAction.OnTextChanged -> onTextChanged(action.text)
            ChatDetailAction.OnBlockUserClick -> onBlockUserClick()
            ChatDetailAction.OnConfirmBlockUser -> confirmBlockUser()
            ChatDetailAction.OnDismissBlockDialog -> _state.update { it.copy(showBlockDialog = false) }
            ChatDetailAction.OnDeleteMatchClick -> onDeleteMatchClick()
            ChatDetailAction.OnConfirmDeleteMatch -> confirmDeleteMatch()
            ChatDetailAction.OnDismissDeleteMatchDialog -> _state.update { it.copy(showDeleteMatchDialog = false) }
            ChatDetailAction.OnReportUserClick -> onReportUserClick()
            is ChatDetailAction.OnSubmitReport -> submitReport(action.reason, action.description)
            ChatDetailAction.OnDismissReportSheet -> _state.update { it.copy(showReportSheet = false) }
            else -> Unit
        }
    }

    private fun updateNearBottom(firstVisibleIndex: Int) {
        _state.update {
            it.copy(
                isNearBottom = firstVisibleIndex <= 3
            )
        }
    }

    private fun updateBanner(topVisibleIndex: Int) {
        val visibleDate = calculateBannerDateFromIndex(
            messages = state.value.messages,
            index = topVisibleIndex
        )

        _state.update {
            it.copy(
                bannerState = BannerState(
                    formattedDate = visibleDate,
                    isVisible = visibleDate != null
                )
            )
        }
    }

    private fun calculateBannerDateFromIndex(
        messages: List<MessageUi>,
        index: Int
    ): UiText? {
        if (messages.isEmpty() || index < 0 || index >= messages.size) {
            return null
        }

        val nearestDateSeparator = (index until messages.size)
            .asSequence()
            .mapNotNull { index ->
                val item = messages.getOrNull(index)
                if (item is MessageUi.DateSeparator) item.date else null
            }
            .firstOrNull()

        return when (nearestDateSeparator) {
            is UiText.Resource -> {
                if (nearestDateSeparator.id == Res.string.today) null else nearestDateSeparator
            }

            else -> nearestDateSeparator
        }
    }

    private fun hideBanner() {
        _state.update {
            it.copy(
                bannerState = it.bannerState.copy(
                    isVisible = false
                )
            )
        }
    }

    private fun retryPagination() = loadNextItems()

    private fun onScrollToTop() = loadNextItems()

    private fun loadNextItems() {
        viewModelScope.launch {
            currentPaginator?.loadNextItems()
        }
    }

    private fun onDismissMessageMenu() {
        _state.update {
            it.copy(
                messageWithOpenMenu = null
            )
        }
    }

    private fun onMessageLongClick(message: MessageUi.LocalUserMessage) {
        _state.update {
            it.copy(
                messageWithOpenMenu = message
            )
        }
    }

    private fun deleteMessage(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository
                .deleteMessage(message.id)
                .onFailure { error ->
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun retryMessage(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository
                .retryMessage(message.id)
                .onFailure { error ->
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun sendMessage() {
        val currentChatId = _chatId.value
        val content = state.value.messageTextFieldState.text.toString().trim()
        if (content.isBlank() || currentChatId == null) {
            return
        }

        viewModelScope.launch {
            val message = OutgoingNewMessage(
                chatId = currentChatId,
                messageId = Uuid.random().toString(),
                content = content
            )

            messageRepository
                .sendMessage(message)
                .onSuccess {
                    state.value.messageTextFieldState.clearText()
                }
                .onFailure { error ->
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun observeCanSendMessage() {
        canSendMessage.onEach { canSend ->
            _state.update {
                it.copy(canSendMessage = canSend)
            }
        }.launchIn(viewModelScope)
    }

    private fun observeChatMessages() {
        val currentMessages = state
            .map { it.messages }
            .distinctUntilChanged()

        val newMessages = _chatId.flatMapLatest { chatId ->
            if (chatId != null) {
                messageRepository.getMessagesForChat(chatId)
            } else emptyFlow()
        }

        val isNearBottom = state.map { it.isNearBottom }.distinctUntilChanged()

        combine(
            currentMessages,
            newMessages,
            isNearBottom
        ) { currentMessages, newMessages, isNearBottom ->
            val newestMessageId = newMessages.firstOrNull()?.message?.id
            val currentNewestId = currentMessages
                .asSequence()
                .filterNot { it is MessageUi.DateSeparator }
                .firstOrNull()
                ?.id

            if (newestMessageId != null && newestMessageId != currentNewestId && isNearBottom) {
                eventChannel.send(ChatDetailEvent.OnNewMessage)
                markMessagesAsRead()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeTypingIndicators() {
        connectionClient
            .typingIndicators
            .onEach { indicator ->
                val currentChatId = _chatId.value ?: return@onEach
                if (indicator.chatId != currentChatId) return@onEach

                val username = state.value.chatUi
                    ?.otherParticipants
                    ?.firstOrNull { it.id == indicator.userId }
                    ?.username
                    ?: return@onEach

                typingUsers.update { it + (indicator.userId to username) }

                typingTimeoutJobs[indicator.userId]?.cancel()
                typingTimeoutJobs[indicator.userId] = viewModelScope.launch {
                    delay(TYPING_TIMEOUT_MS)
                    typingUsers.update { it - indicator.userId }
                    typingTimeoutJobs.remove(indicator.userId)
                }
            }
            .launchIn(viewModelScope)

        connectionClient
            .chatMessages
            .onEach { message ->
                typingUsers.update { it - message.senderId }
                typingTimeoutJobs[message.senderId]?.cancel()
                typingTimeoutJobs.remove(message.senderId)
            }
            .launchIn(viewModelScope)
    }

    private fun observeMessagesRead() {
        connectionClient
            .messagesRead
            .onEach { event ->
                val currentChatId = _chatId.value ?: return@onEach
                if (event.chatId != currentChatId) return@onEach

                event.messageIds.forEach { messageId ->
                    messageRepository.updateMessageDeliveryStatus(
                        messageId = messageId,
                        status = ChatMessageDeliveryStatus.READ
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun markMessagesAsRead() {
        val chatId = _chatId.value ?: return
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull() ?: return@launch
            val localUserId = authInfo.user.id

            val unreadMessageIds = messageRepository.getMessagesForChat(chatId)
                .firstOrNull()
                ?.filter { it.sender.userId != localUserId && it.message.deliveryStatus == ChatMessageDeliveryStatus.SENT }
                ?.map { it.message.id }
                ?: return@launch

            if (unreadMessageIds.isEmpty()) return@launch

            connectionClient.sendReadReceipt(chatId, unreadMessageIds)

            unreadMessageIds.forEach { messageId ->
                messageRepository.updateMessageDeliveryStatus(
                    messageId = messageId,
                    status = ChatMessageDeliveryStatus.READ
                )
            }
        }
    }

    private fun onTextChanged(text: String) {
        val chatId = _chatId.value ?: return
        if (text.isEmpty()) return

        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        if (now - lastTypingSentMs < TYPING_DEBOUNCE_MS) return
        lastTypingSentMs = now

        viewModelScope.launch {
            connectionClient.sendTyping(chatId)
        }
    }

    private fun clearTypingState() {
        typingTimeoutJobs.values.forEach { it.cancel() }
        typingTimeoutJobs.clear()
        typingUsers.update { emptyMap() }
        lastTypingSentMs = 0L
    }

    private fun observeConnectionState() {
        connectionClient
            .connectionState
            .onEach { connectionState ->
                if (connectionState == ConnectionState.CONNECTED) {
                    clearTypingState()
                    currentPaginator?.loadNextItems()
                }

                _state.update {
                    it.copy(connectionState = connectionState)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setupPaginatorForChat(chatId: String) {
        currentPaginator = Paginator(
            initialKey = null,
            onLoadUpdated = { isLoading ->
                _state.update { it.copy(isPaginationLoading = isLoading) }
            },
            onRequest = { beforeTimestamp ->
                messageRepository.fetchMessages(chatId, beforeTimestamp)
            },
            getNextKey = { messages ->
                messages.minOfOrNull { it.createdAt }?.toString()
            },
            onError = { throwable ->
                if (throwable is DataErrorException) {
                    _state.update {
                        it.copy(
                            paginationError = throwable.error.toUiText()
                        )
                    }
                }
            },
            onSuccess = { messages, _ ->
                _state.update {
                    it.copy(
                        endReached = messages.isEmpty(),
                        paginationError = null
                    )
                }
            }
        )

        _state.update {
            it.copy(
                endReached = false,
                isPaginationLoading = false,
            )
        }
    }

    private fun onLeaveChatClick() {
        val chatId = _chatId.value ?: return

        _state.update {
            it.copy(isChatOptionsOpen = false)
        }

        viewModelScope.launch {
            chatRepository
                .leaveChat(chatId)
                .onSuccess {
                    _state.value.messageTextFieldState.clearText()

                    _chatId.update { null }
                    _state.update {
                        it.copy(
                            chatUi = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }

                    eventChannel.send(
                        ChatDetailEvent.OnChatLeft
                    )
                }
                .onFailure { error ->
                    eventChannel.send(
                        ChatDetailEvent.OnError(
                            error.toUiText()
                        )
                    )
                }
        }
    }

    private fun onBlockUserClick() {
        _state.update { it.copy(isChatOptionsOpen = false, showBlockDialog = true) }
    }

    private fun confirmBlockUser() {
        val otherUserId = state.value.chatUi?.otherParticipants?.firstOrNull()?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isBlocking = true) }
            blockService.blockUser(otherUserId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isBlocking = false,
                            showBlockDialog = false,
                            chatUi = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }
                    state.value.messageTextFieldState.clearText()
                    _chatId.update { null }
                    eventChannel.send(ChatDetailEvent.OnUserBlocked)
                }
                .onFailure { error ->
                    _state.update { it.copy(isBlocking = false, showBlockDialog = false) }
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun onDeleteMatchClick() {
        _state.update { it.copy(isChatOptionsOpen = false, showDeleteMatchDialog = true) }
    }

    private fun confirmDeleteMatch() {
        val otherUserId = state.value.chatUi?.otherParticipants?.firstOrNull()?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeletingMatch = true) }
            matchingService.deleteMatch(otherUserId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isDeletingMatch = false,
                            showDeleteMatchDialog = false,
                            chatUi = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }
                    state.value.messageTextFieldState.clearText()
                    _chatId.update { null }
                    eventChannel.send(ChatDetailEvent.OnMatchDeleted)
                }
                .onFailure { error ->
                    _state.update { it.copy(isDeletingMatch = false, showDeleteMatchDialog = false) }
                    eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                }
        }
    }

    private fun onDismissChatOptions() {
        _state.update {
            it.copy(
                isChatOptionsOpen = false
            )
        }
    }

    private fun onChatOptionsClick() {
        _state.update {
            it.copy(isChatOptionsOpen = true)
        }
    }

    private fun switchChat(chatId: String?) {
        clearTypingState()
        _chatId.update { chatId }
        viewModelScope.launch {
            chatId?.let {
                chatRepository.fetchChatById(chatId)
                markMessagesAsRead()
            }
        }
    }

    private fun onReportUserClick() {
        _state.update { it.copy(isChatOptionsOpen = false, showReportSheet = true) }
    }

    private fun submitReport(reason: ReportReason, description: String?) {
        val otherUserId = state.value.chatUi?.otherParticipants?.firstOrNull()?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReport = true) }
            reportService.reportUser(otherUserId, reason, description)
                .onSuccess {
                    _state.update { it.copy(isSubmittingReport = false, showReportSheet = false) }
                    eventChannel.send(ChatDetailEvent.OnReportSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmittingReport = false, showReportSheet = false) }
                    when (error) {
                        DataError.Remote.CONFLICT -> eventChannel.send(
                            ChatDetailEvent.OnError(UiText.Resource(Res.string.report_duplicate))
                        )
                        DataError.Remote.FORBIDDEN -> eventChannel.send(ChatDetailEvent.OnForceLogout)
                        else -> eventChannel.send(ChatDetailEvent.OnError(error.toUiText()))
                    }
                }
        }
    }

    companion object {
        private const val TYPING_DEBOUNCE_MS = 3000L
        private const val TYPING_TIMEOUT_MS = 5000L
    }
}
