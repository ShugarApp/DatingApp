package com.dating.home.presentation.chat.mappers

import com.dating.home.data.dto.DateProposalContentDto
import com.dating.home.domain.models.DateProposalStatus
import com.dating.home.domain.models.MessageType
import com.dating.home.domain.models.MessageWithSender
import com.dating.home.domain.models.ReactionSummary
import com.dating.home.presentation.chat.model.DateProposalUi
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.home.presentation.chat.util.DateUtils
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

private val proposalJson = Json { ignoreUnknownKeys = true }

fun List<MessageWithSender>.toUiList(
    localUserId: String,
    reactionsMap: Map<String, List<ReactionSummary>> = emptyMap()
): List<MessageUi> {
    return this
        .sortedByDescending { it.message.createdAt }
        .groupBy {
            it.message.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toUi(localUserId, reactionsMap) } + MessageUi.DateSeparator(
                id = date.toString(),
                date = DateUtils.formatDateSeparator(date)
            )
        }
}

fun MessageWithSender.toUi(
    localUserId: String,
    reactionsMap: Map<String, List<ReactionSummary>> = emptyMap()
): MessageUi {
    val isFromLocalUser = this.sender.userId == localUserId
    val reactions = reactionsMap[message.id] ?: emptyList()
    val dateProposal = if (message.messageType == MessageType.DATE_PROPOSAL) {
        parseDateProposal(message.id, message.content, isFromLocalUser)
    } else null

    return if(isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            messageType = message.messageType,
            reactions = reactions,
            dateProposal = dateProposal
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = sender.toUi(),
            messageType = message.messageType,
            reactions = reactions,
            dateProposal = dateProposal
        )
    }
}

private fun parseDateProposal(
    messageId: String,
    content: String,
    isFromLocalUser: Boolean
): DateProposalUi? {
    return try {
        val dto = proposalJson.decodeFromString<DateProposalContentDto>(content)
        val status = try {
            DateProposalStatus.valueOf(dto.status)
        } catch (_: IllegalArgumentException) {
            DateProposalStatus.PENDING
        }
        val isPending = status == DateProposalStatus.PENDING
        DateProposalUi(
            messageId = messageId,
            dateTime = dto.dateTime,
            location = dto.location,
            status = status,
            canAccept = isPending && !isFromLocalUser,
            canReject = isPending && !isFromLocalUser,
            canCancel = isPending && isFromLocalUser,
            canEdit = isPending && !isFromLocalUser
        )
    } catch (_: Exception) {
        null
    }
}
