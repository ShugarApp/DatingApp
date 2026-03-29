package com.dating.home.data.matching

import com.dating.core.data.dto.UserSerializable
import com.dating.core.data.mappers.toDomain
import com.dating.core.data.networking.delete
import com.dating.core.data.networking.get
import com.dating.core.data.networking.post
import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.home.data.dto.request.SwipeRequest
import com.dating.home.data.dto.response.SwipeResponse
import com.dating.home.domain.matching.MatchingService
import com.dating.home.domain.matching.SwipeAction
import com.dating.home.domain.matching.SwipeResult
import io.ktor.client.HttpClient

class KtorMatchingService(private val httpClient: HttpClient) : MatchingService {

    override suspend fun getFeed(
        gender: String?,
        minAge: Int?,
        maxAge: Int?,
        maxDistance: Double?,
        page: Int,
        size: Int
    ): Result<List<User>, DataError.Remote> {
        val params = buildMap<String, Any> {
            if (gender != null) put("gender", gender)
            if (minAge != null) put("minAge", minAge)
            if (maxAge != null) put("maxAge", maxAge)
            if (maxDistance != null) put("maxDistance", maxDistance)
            put("page", page)
            put("size", size)
        }
        return httpClient.get<List<UserSerializable>>(
            route = "/matching/feed",
            queryParams = params
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun swipe(
        swipedId: String,
        action: SwipeAction
    ): Result<SwipeResult, DataError.Remote> {
        return httpClient.post<SwipeRequest, SwipeResponse>(
            route = "/matching/swipe",
            body = SwipeRequest(swipedId = swipedId, action = action.name)
        ).map { SwipeResult(isMatch = it.isMatch) }
    }

    override suspend fun getMatches(): Result<List<User>, DataError.Remote> {
        return httpClient.get<List<UserSerializable>>(
            route = "/matching/matches"
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getLikes(): Result<List<User>, DataError.Remote> {
        return httpClient.get<List<UserSerializable>>(
            route = "/matching/likes"
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun deleteMatch(matchedUserId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/matching/matches/$matchedUserId"
        )
    }

    override suspend fun undoSwipe(swipedId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/matching/swipe/$swipedId"
        )
    }
}
