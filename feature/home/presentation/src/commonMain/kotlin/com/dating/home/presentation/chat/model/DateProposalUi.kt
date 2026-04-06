package com.dating.home.presentation.chat.model

import com.dating.home.domain.models.DateProposalStatus

data class DateProposalUi(
    val messageId: String,
    val dateTime: String,
    val location: String,
    val status: DateProposalStatus,
    val canAccept: Boolean,
    val canReject: Boolean,
    val canCancel: Boolean,
    val canEdit: Boolean
)
