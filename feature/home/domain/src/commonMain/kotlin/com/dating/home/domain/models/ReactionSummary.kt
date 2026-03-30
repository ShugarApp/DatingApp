package com.dating.home.domain.models

data class ReactionSummary(
    val emoji: String,
    val count: Int,
    val reactedByMe: Boolean
)
