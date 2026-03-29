package com.dating.home.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.onFailure
import com.dating.core.domain.util.onSuccess
import com.dating.core.presentation.util.toUiText
import com.dating.home.domain.block.BlockService
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.matching.SwipeAction
import com.dating.home.domain.report.ReportReason
import com.dating.home.domain.report.ReportService
import com.dating.home.domain.user.UserService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val userService: UserService,
    private val matchingService: MatchingService,
    private val blockService: BlockService,
    private val reportService: ReportService
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileDetailEvent>()
    val events = _events.receiveAsFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            userService.getUserById(userId)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    fun onAction(action: ProfileDetailAction) {
        when (action) {
            is ProfileDetailAction.OnBack -> {
                viewModelScope.launch { _events.send(ProfileDetailEvent.NavigateBack()) }
            }
            is ProfileDetailAction.OnSwipeRight -> swipe(action.userId, SwipeAction.LIKE)
            is ProfileDetailAction.OnSwipeLeft -> swipe(action.userId, SwipeAction.DISLIKE)
            is ProfileDetailAction.OnBlockClick -> {
                _state.update { it.copy(showBlockDialog = true) }
            }
            ProfileDetailAction.OnConfirmBlock -> confirmBlock()
            ProfileDetailAction.OnDismissBlockDialog -> {
                _state.update { it.copy(showBlockDialog = false) }
            }
            is ProfileDetailAction.OnDeleteMatchClick -> {
                _state.update { it.copy(showDeleteMatchDialog = true) }
            }
            ProfileDetailAction.OnConfirmDeleteMatch -> confirmDeleteMatch()
            ProfileDetailAction.OnDismissDeleteMatchDialog -> {
                _state.update { it.copy(showDeleteMatchDialog = false) }
            }
            is ProfileDetailAction.OnReportClick -> {
                _state.update { it.copy(showReportSheet = true) }
            }
            is ProfileDetailAction.OnSubmitReport -> submitReport(action.reason, action.description)
            ProfileDetailAction.OnDismissReportSheet -> {
                _state.update { it.copy(showReportSheet = false) }
            }
        }
    }

    private fun confirmBlock() {
        val userId = _state.value.user?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isBlocking = true) }
            blockService.blockUser(userId)
                .onSuccess {
                    _state.update { it.copy(isBlocking = false, showBlockDialog = false) }
                    _events.send(ProfileDetailEvent.OnUserBlocked)
                }
                .onFailure {
                    _state.update { it.copy(isBlocking = false, showBlockDialog = false) }
                    _events.send(ProfileDetailEvent.NavigateBack())
                }
        }
    }

    private fun confirmDeleteMatch() {
        val userId = _state.value.user?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeletingMatch = true) }
            matchingService.deleteMatch(userId)
                .onSuccess {
                    _state.update { it.copy(isDeletingMatch = false, showDeleteMatchDialog = false) }
                    _events.send(ProfileDetailEvent.OnMatchDeleted)
                }
                .onFailure {
                    _state.update { it.copy(isDeletingMatch = false, showDeleteMatchDialog = false) }
                    _events.send(ProfileDetailEvent.NavigateBack())
                }
        }
    }

    private fun submitReport(reason: ReportReason, description: String?) {
        val userId = _state.value.user?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReport = true) }
            reportService.reportUser(userId, reason, description)
                .onSuccess { result ->
                    _state.update { it.copy(isSubmittingReport = false, showReportSheet = false) }
                    _events.send(ProfileDetailEvent.OnReportSuccess(result.message))
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmittingReport = false, showReportSheet = false) }
                    when (error) {
                        DataError.Remote.CONFLICT -> _events.send(
                            ProfileDetailEvent.OnReportError("duplicate")
                        )
                        DataError.Remote.FORBIDDEN -> _events.send(ProfileDetailEvent.OnForceLogout)
                        else -> _events.send(ProfileDetailEvent.NavigateBack())
                    }
                }
        }
    }

    private fun swipe(userId: String, action: SwipeAction) {
        val isDislike = action == SwipeAction.DISLIKE
        viewModelScope.launch {
            matchingService.swipe(swipedId = userId, action = action)
                .onSuccess { result ->
                    if (result.isMatch) {
                        val name = _state.value.user?.username ?: ""
                        _events.send(ProfileDetailEvent.ShowMatch(name, userId))
                    } else {
                        _events.send(ProfileDetailEvent.NavigateBack(swipedUserId = userId, isDislike = isDislike))
                    }
                }
                .onFailure {
                    _events.send(ProfileDetailEvent.NavigateBack(swipedUserId = userId, isDislike = isDislike))
                }
        }
    }
}
