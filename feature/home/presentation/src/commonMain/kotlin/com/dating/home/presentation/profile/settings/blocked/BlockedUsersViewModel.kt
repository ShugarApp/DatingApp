package com.dating.home.presentation.profile.settings.blocked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.UiText
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.block.BlockService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockedUsersViewModel(
    private val blockService: BlockService
) : ViewModel() {

    private val _state = MutableStateFlow(BlockedUsersState())
    val state = _state.asStateFlow()

    private val _events = Channel<BlockedUsersEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadBlockedUsers()
    }

    fun onAction(action: BlockedUsersAction) {
        when (action) {
            BlockedUsersAction.OnRefresh -> loadBlockedUsers()
            is BlockedUsersAction.OnUnblockClick -> {
                _state.update {
                    it.copy(showUnblockDialog = true, userToUnblock = action.user)
                }
            }
            BlockedUsersAction.OnConfirmUnblock -> confirmUnblock()
            BlockedUsersAction.OnDismissUnblockDialog -> {
                _state.update {
                    it.copy(showUnblockDialog = false, userToUnblock = null)
                }
            }
        }
    }

    private fun loadBlockedUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            blockService.getBlockedUsers()
                .onSuccess { users ->
                    _state.update { it.copy(isLoading = false, blockedUsers = users) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun confirmUnblock() {
        val user = _state.value.userToUnblock ?: return
        viewModelScope.launch {
            _state.update { it.copy(isUnblocking = true) }
            blockService.unblockUser(user.userId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isUnblocking = false,
                            showUnblockDialog = false,
                            userToUnblock = null,
                            blockedUsers = it.blockedUsers.filter { u -> u.userId != user.userId }
                        )
                    }
                    _events.send(
                        BlockedUsersEvent.ShowToast(UiText.DynamicString("Usuario desbloqueado"))
                    )
                }
                .onFailure { error ->
                    _state.update { it.copy(isUnblocking = false, showUnblockDialog = false, userToUnblock = null) }
                    loadBlockedUsers()
                }
        }
    }
}
