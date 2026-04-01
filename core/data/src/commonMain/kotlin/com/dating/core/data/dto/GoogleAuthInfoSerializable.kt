package com.dating.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthInfoSerializable(
    val accessToken: String,
    val refreshToken: String,
    val user: UserSerializable,
    val isNewUser: Boolean = false
)
