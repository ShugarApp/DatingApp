package com.dating.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DateProposalContentDto(
    val dateTime: String,
    val location: DateProposalLocationDto,
    val status: String,
    val previousProposalMessageId: String? = null
)
