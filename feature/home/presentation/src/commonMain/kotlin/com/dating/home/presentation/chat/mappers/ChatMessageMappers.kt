package com.dating.home.presentation.chat.mappers

import com.dating.home.domain.models.MessageWithSender
import com.dating.home.domain.models.ReactionSummary
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.home.presentation.chat.util.DateUtils
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
    return if(isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            messageType = message.messageType,
            reactions = reactions
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = sender.toUi(),
            messageType = message.messageType,
            reactions = reactions
        )
    }
}
