package com.dating.home.presentation.chat.chat_list

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.util.onFailure
import com.dating.home.domain.chat.ChatRepository
import com.dating.home.domain.participant.ChatParticipantRepository
import com.dating.home.presentation.chat.mappers.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val repository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val chatParticipantRepository: ChatParticipantRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListState())
    val state = combine(
        _state,
        repository.getChats(),
        sessionStorage.observeAuthInfo()
    ) { currentState, chats, authInfo ->
        if (authInfo == null) {
            return@combine ChatListState()
        }

        currentState.copy(
            chats = chats.mapNotNull { it.toUi(authInfo.user.id) },
            localParticipant = authInfo.user.toUi()
        )
    }
        .onStart {
            if (!hasLoadedInitialData) {
                observeSearchTextField()
                loadChats()
                fetchLocalUserProfile()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListState()
        )

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnSelectChat -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListAction.OnRefresh -> refresh()

            is ChatListAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
            }

            is ChatListAction.OnSwipeToDeleteChat -> {
                _state.update { it.copy(showDeleteConfirmationForChatId = action.chatId) }
            }

            ChatListAction.OnConfirmDeleteChat -> confirmDeleteChat()

            ChatListAction.OnDismissDeleteChatDialog -> {
                _state.update { it.copy(showDeleteConfirmationForChatId = null) }
            }

            else -> Unit
        }
    }

    private fun observeSearchTextField() {
        snapshotFlow { _state.value.searchTextFieldState.text.toString() }
            .onEach { text ->
                _state.update { it.copy(searchQuery = text) }
            }
            .launchIn(viewModelScope)
    }

    private fun confirmDeleteChat() {
        val chatId = _state.value.showDeleteConfirmationForChatId ?: return
        viewModelScope.launch {
            _state.update { it.copy(showDeleteConfirmationForChatId = null) }
            repository.leaveChat(chatId)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.fetchChats()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun fetchLocalUserProfile() {
        viewModelScope.launch {
            chatParticipantRepository.fetchLocalParticipant()
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.fetchChats()
        }
    }
}
