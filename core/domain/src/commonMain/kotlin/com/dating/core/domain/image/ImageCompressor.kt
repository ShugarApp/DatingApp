package com.dating.core.domain.image

interface ImageCompressor {
    suspend fun compressImage(
        bytes: ByteArray,
        mimeType: String,
        maxWidthPx: Int = 1080,
        maxHeightPx: Int = 1080,
        quality: Int = 82
    ): CompressedImage
}

data class CompressedImage(
    val bytes: ByteArray,
    val mimeType: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val widthPx: Int,
    val heightPx: Int
)
