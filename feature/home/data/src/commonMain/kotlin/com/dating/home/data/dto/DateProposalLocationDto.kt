package com.dating.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DateProposalLocationDto(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null
)
