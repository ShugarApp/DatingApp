package com.dating.home.data.message

import com.dating.core.data.networking.constructRoute
import com.dating.core.data.networking.safeCall
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.home.data.dto.MediaUploadUrlDto
import com.dating.home.data.mappers.toDomain
import com.dating.home.domain.message.ChatMediaService
import com.dating.home.domain.models.MediaUploadInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class KtorChatMediaService(
    private val httpClient: HttpClient
) : ChatMediaService {

    override suspend fun getUploadUrl(
        chatId: String,
        mimeType: String
    ): Result<MediaUploadInfo, DataError.Remote> {
        return safeCall<MediaUploadUrlDto> {
            httpClient.post {
                url(constructRoute("/chat/$chatId/media/upload-url"))
                parameter("mimeType", mimeType)
            }
        }.let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Failure -> Result.Failure(result.error)
            }
        }
    }

    override suspend fun uploadMedia(
        uploadUrl: String,
        headers: Map<String, String>,
        data: ByteArray
    ): EmptyResult<DataError.Remote> {
        return safeCall<Unit> {
            httpClient.put {
                url(uploadUrl)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(data)
            }
        }
    }
}
