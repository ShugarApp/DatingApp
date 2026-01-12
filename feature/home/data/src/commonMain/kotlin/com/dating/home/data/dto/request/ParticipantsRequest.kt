package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantsRequest(
    val userIds: List<String>
)
