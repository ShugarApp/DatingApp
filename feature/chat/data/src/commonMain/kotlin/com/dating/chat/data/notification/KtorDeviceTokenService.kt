package com.dating.chat.data.notification

import com.dating.chat.data.dto.request.RegisterDeviceTokenRequest
import com.dating.chat.domain.notification.DeviceTokenService
import com.dating.core.data.networking.delete
import com.dating.core.data.networking.post
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorDeviceTokenService(
    private val httpClient: HttpClient
): DeviceTokenService {

    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/register",
            body = RegisterDeviceTokenRequest(
                token = token,
                platform = platform
            )
        )
    }

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/notification/$token"
        )
    }
}