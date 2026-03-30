package com.dating.home.domain.models

data class MediaUploadInfo(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: String
)
