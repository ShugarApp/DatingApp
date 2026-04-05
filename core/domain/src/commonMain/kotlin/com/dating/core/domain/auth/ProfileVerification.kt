package com.dating.core.domain.auth

data class ProfileVerification(
    val id: String,
    val status: VerificationStatus,
    val rejectionReason: String? = null,
    val faceConfidence: Double? = null,
    val matchScore: Double? = null,
    val attemptNumber: Int = 1,
    val createdAt: String? = null
)
