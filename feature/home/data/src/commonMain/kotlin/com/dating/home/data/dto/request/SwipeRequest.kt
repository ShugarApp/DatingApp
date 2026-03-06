package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SwipeRequest(
    val swipedId: String,
    val action: String
)
