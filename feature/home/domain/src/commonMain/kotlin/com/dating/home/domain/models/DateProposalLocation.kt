package com.dating.home.domain.models

data class DateProposalLocation(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null
)
