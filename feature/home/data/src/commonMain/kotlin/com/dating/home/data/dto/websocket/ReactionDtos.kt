package com.dating.home.data.dto.websocket

import kotlinx.serialization.Serializable

@Serializable
data class ReactionSummaryDto(
    val emoji: String,
    val count: Int,
    val reactedByMe: Boolean
)

@Serializable
data class MessageReactionUpdatedPayload(
    val messageId: String,
    val chatId: String,
    val reactions: List<ReactionSummaryDto>
)
