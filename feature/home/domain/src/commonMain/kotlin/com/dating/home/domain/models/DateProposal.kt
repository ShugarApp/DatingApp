package com.dating.home.domain.models

data class DateProposal(
    val dateTime: String,
    val location: String,
    val status: DateProposalStatus,
    val previousProposalMessageId: String? = null
)
