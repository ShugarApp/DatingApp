package com.dating.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationContentDto(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val address: String? = null
)
