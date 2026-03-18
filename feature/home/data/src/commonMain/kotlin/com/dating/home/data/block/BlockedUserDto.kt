package com.dating.home.data.block

import com.dating.home.domain.block.BlockedUser
import kotlinx.serialization.Serializable

@Serializable
data class BlockedUserDto(
    val userId: String,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val blockedAt: String
) {
    fun toDomain(): BlockedUser = BlockedUser(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl,
        blockedAt = blockedAt
    )
}
