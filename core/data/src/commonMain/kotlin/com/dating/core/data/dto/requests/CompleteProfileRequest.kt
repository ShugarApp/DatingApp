package com.dating.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class CompleteProfileRequest(
    val username: String,
    val birthDate: String,
    val gender: String,
    val interestedIn: String,
    val lookingFor: String
)
