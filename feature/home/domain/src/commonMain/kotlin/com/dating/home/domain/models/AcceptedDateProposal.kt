package com.dating.home.domain.models

data class AcceptedDateProposal(
    val messageId: String,
    val chatId: String,
    val dateTime: String,
    val location: DateProposalLocation,
    val status: DateProposalStatus,
    val isSentByMe: Boolean,
    val otherPersonName: String,
    val otherPersonAvatarUrl: String?
)
