package com.dating.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val birthDate: String? = null,
    val gender: String? = null,
    val interestedIn: String? = null,
    val lookingFor: String? = null
)
