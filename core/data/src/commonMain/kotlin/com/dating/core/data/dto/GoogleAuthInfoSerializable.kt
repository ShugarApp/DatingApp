package com.dating.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthInfoSerializable(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: UserSerializable? = null,
    val email: String? = null,
    val isNewUser: Boolean = false
)
