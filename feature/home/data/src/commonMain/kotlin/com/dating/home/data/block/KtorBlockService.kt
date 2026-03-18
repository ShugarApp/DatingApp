package com.dating.home.data.block

import com.dating.core.data.networking.delete
import com.dating.core.data.networking.get
import com.dating.core.data.networking.post
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.home.domain.block.BlockService
import com.dating.home.domain.block.BlockedUser
import io.ktor.client.HttpClient

class KtorBlockService(private val httpClient: HttpClient) : BlockService {

    override suspend fun blockUser(userId: String): EmptyResult<DataError.Remote> {
        return httpClient.post<Unit, Unit>(
            route = "/users/$userId/block",
            body = Unit
        )
    }

    override suspend fun unblockUser(userId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/users/$userId/block"
        )
    }

    override suspend fun getBlockedUsers(): Result<List<BlockedUser>, DataError.Remote> {
        return httpClient.get<List<BlockedUserDto>>(
            route = "/users/blocked"
        ).map { dtos -> dtos.map { it.toDomain() } }
    }
}
