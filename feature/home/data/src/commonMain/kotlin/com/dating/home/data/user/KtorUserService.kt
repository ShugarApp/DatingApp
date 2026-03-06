package com.dating.home.data.user

import com.dating.core.data.dto.UserSerializable
import com.dating.core.data.mappers.toDomain
import com.dating.core.data.networking.get
import com.dating.core.data.networking.put
import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.home.data.dto.request.LocationRequest
import com.dating.home.domain.user.UserService
import io.ktor.client.HttpClient

class KtorUserService(private val httpClient: HttpClient) : UserService {

    override suspend fun updateLocation(
        latitude: Double,
        longitude: Double
    ): Result<User, DataError.Remote> {
        return httpClient.put<LocationRequest, UserSerializable>(
            route = "/users/profile/location",
            body = LocationRequest(latitude = latitude, longitude = longitude)
        ).map { it.toDomain() }
    }

    override suspend fun getUserById(id: String): Result<User, DataError.Remote> {
        return httpClient.get<UserSerializable>(
            route = "/users/$id"
        ).map { it.toDomain() }
    }
}
