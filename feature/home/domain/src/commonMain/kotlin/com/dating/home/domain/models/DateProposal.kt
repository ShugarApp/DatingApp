package com.dating.home.domain.models

data class DateProposal(
    val dateTime: String,
    val location: DateProposalLocation,
    val status: DateProposalStatus,
    val previousProposalMessageId: String? = null
)
