package com.dating.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DateProposalContentDto(
    val dateTime: String,
    val location: String,
    val status: String,
    val previousProposalMessageId: String? = null
)
