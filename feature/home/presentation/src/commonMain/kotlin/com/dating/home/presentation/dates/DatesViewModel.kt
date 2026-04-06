package com.dating.home.presentation.dates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dating.home.domain.message.MessageRepository
import com.dating.home.domain.models.DateProposalLocation
import com.dating.home.domain.models.DateProposalStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalUuidApi::class)
class DatesViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    val state = messageRepository
        .getActiveDateProposals()
        .map { proposals -> DatesState(dates = proposals, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DatesState(isLoading = true)
        )

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
