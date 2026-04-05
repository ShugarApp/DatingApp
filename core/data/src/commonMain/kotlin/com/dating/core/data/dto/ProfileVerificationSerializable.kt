package com.dating.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileVerificationSerializable(
    val id: String,
    val status: String,
    val rejectionReason: String? = null,
    val faceConfidence: Double? = null,
    val matchScore: Double? = null,
    val attemptNumber: Int = 1,
    val createdAt: String? = null
)
