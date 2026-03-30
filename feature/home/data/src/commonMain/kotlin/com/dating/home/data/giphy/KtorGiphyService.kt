package com.dating.home.data.giphy

import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result
import com.dating.home.domain.giphy.GiphyGif
import com.dating.home.domain.giphy.GiphyService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorGiphyService(
    private val httpClient: HttpClient
) : GiphyService {

    override suspend fun trending(offset: Int, limit: Int): Result<List<GiphyGif>, DataError.Remote> {
        return try {
            val response = httpClient.get(GIPHY_BASE_URL + "trending") {
                parameter("api_key", API_KEY)
                parameter("limit", limit)
                parameter("offset", offset)
                parameter("rating", "pg-13")
            }
            val body = response.body<GiphyResponse>()
            Result.Success(body.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun search(query: String, offset: Int, limit: Int): Result<List<GiphyGif>, DataError.Remote> {
        return try {
            val response = httpClient.get(GIPHY_BASE_URL + "search") {
                parameter("api_key", API_KEY)
                parameter("q", query)
                parameter("limit", limit)
                parameter("offset", offset)
                parameter("rating", "pg-13")
            }
            val body = response.body<GiphyResponse>()
            Result.Success(body.data.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Failure(DataError.Remote.UNKNOWN)
        }
    }

    companion object {
        private const val API_KEY = "Wf3Jh01f98ja8yo0DPTnunvFXzQawDdE"
        private const val GIPHY_BASE_URL = "https://api.giphy.com/v1/gifs/"
    }
}

private fun GiphyGifDto.toDomain(): GiphyGif {
    return GiphyGif(
        id = id,
        previewUrl = images.fixedWidth.url,
        originalUrl = images.original.url,
        width = images.fixedWidth.width.toIntOrNull() ?: 200,
        height = images.fixedWidth.height.toIntOrNull() ?: 200
    )
}
