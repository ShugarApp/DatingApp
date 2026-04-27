package com.dating.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class GoogleRegisterRequest(
    val idToken: String,
    val username: String,
    val birthDate: String,
    val gender: String,
    val interestedIn: String,
    val lookingFor: String,
    val idealDate: String? = null
)
