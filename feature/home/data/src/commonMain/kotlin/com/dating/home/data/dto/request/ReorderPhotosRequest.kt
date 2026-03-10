package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReorderPhotosRequest(
    val photos: List<String>
)
