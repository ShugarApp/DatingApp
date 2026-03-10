package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPhotoRequest(
    val publicUrl: String,
    val index: Int
)
