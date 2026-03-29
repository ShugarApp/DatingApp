package com.dating.home.data.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequestDto(
    val reason: String,
    val description: String? = null
)

@Serializable
data class ReportResponseDto(
    val id: String,
    val message: String
)
