package com.dating.home.domain.matching

import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result

enum class SwipeAction { LIKE, DISLIKE }

data class SwipeResult(val isMatch: Boolean)

interface MatchingService {
    suspend fun getFeed(
        gender: String? = null,
        minAge: Int? = null,
        maxAge: Int? = null,
        maxDistance: Double? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<List<User>, DataError.Remote>

    suspend fun swipe(
        swipedId: String,
        action: SwipeAction
    ): Result<SwipeResult, DataError.Remote>

    suspend fun getMatches(): Result<List<User>, DataError.Remote>

    suspend fun getLikes(): Result<List<User>, DataError.Remote>

    suspend fun deleteMatch(matchedUserId: String): EmptyResult<DataError.Remote>

    suspend fun undoSwipe(swipedId: String): EmptyResult<DataError.Remote>
}
