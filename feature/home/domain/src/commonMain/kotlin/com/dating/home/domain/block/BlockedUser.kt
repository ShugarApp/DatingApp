package com.dating.home.domain.block

data class BlockedUser(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val blockedAt: String
)
