package com.dating.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MediaUploadUrlDto(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: String
)
