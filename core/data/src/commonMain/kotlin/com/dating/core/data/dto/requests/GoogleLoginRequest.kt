package com.dating.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val idToken: String
)
