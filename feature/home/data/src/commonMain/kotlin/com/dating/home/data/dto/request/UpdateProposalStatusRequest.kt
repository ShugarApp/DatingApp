package com.dating.home.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProposalStatusRequest(
    val status: String
)
