package com.dating.home.data.giphy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiphyResponse(
    val data: List<GiphyGifDto>
)

@Serializable
data class GiphyGifDto(
    val id: String,
    val images: GiphyImages
)

@Serializable
data class GiphyImages(
    @SerialName("fixed_width")
    val fixedWidth: GiphyImageVariant,
    val original: GiphyImageVariant
)

@Serializable
data class GiphyImageVariant(
    val url: String,
    val width: String = "0",
    val height: String = "0"
)
