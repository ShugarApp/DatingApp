package com.dating.home.presentation.dates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.home.domain.message.MessageRepository
import com.dating.home.domain.models.DateProposalLocation
import com.dating.home.domain.models.DateProposalStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalUuidApi::class)
class DatesViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(DateFilter.UPCOMING)

    val state = combine(
        messageRepository.getActiveDateProposals(),
        _filter
    ) { proposals, filter ->
        DatesState(dates = proposals, isLoading = false, selectedFilter = filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DatesState(isLoading = true)
    )

    fun selectFilter(filter: DateFilter) {
        _filter.value = filter
    }

    fun cancelDate(messageId: String, chatId: String) {
        viewModelScope.launch {
            messageRepository.updateDateProposalStatus(
                messageId = messageId,
                chatId = chatId,
                newStatus = DateProposalStatus.CANCELLED
            )
        }
    }

    fun acceptDate(messageId: String, chatId: String) {
        viewModelScope.launch {
            messageRepository.updateDateProposalStatus(
                messageId = messageId,
                chatId = chatId,
                newStatus = DateProposalStatus.ACCEPTED
            )
        }
    }

    fun rejectDate(messageId: String, chatId: String) {
        viewModelScope.launch {
            messageRepository.updateDateProposalStatus(
                messageId = messageId,
                chatId = chatId,
                newStatus = DateProposalStatus.REJECTED
            )
        }
    }

    fun editDate(
        oldMessageId: String,
        chatId: String,
        newDateTime: String,
        newLocation: DateProposalLocation
    ) {
        viewModelScope.launch {
            messageRepository.updateDateProposalStatus(
                messageId = oldMessageId,
                chatId = chatId,
                newStatus = DateProposalStatus.EDITED
            )
            messageRepository.sendDateProposal(
                chatId = chatId,
                messageId = Uuid.random().toString(),
                dateTime = newDateTime,
                location = newLocation
            )
        }
    }
}
