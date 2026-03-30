package com.dating.core.data.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.dating.core.domain.image.CompressedImage
import com.dating.core.domain.image.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AndroidImageCompressor : ImageCompressor {

    override suspend fun compressImage(
        bytes: ByteArray,
        mimeType: String,
        maxWidthPx: Int,
        maxHeightPx: Int,
        quality: Int
    ): CompressedImage = withContext(Dispatchers.Default) {
        if (mimeType == "image/gif") {
            return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = 0,
                heightPx = 0
            )
        }

        val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = 0,
                heightPx = 0
            )

        try {
            val (newWidth, newHeight) = calculateScaledSize(
                originalBitmap.width, originalBitmap.height, maxWidthPx, maxHeightPx
            )

            val scaledBitmap = if (newWidth != originalBitmap.width || newHeight != originalBitmap.height) {
                Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } else {
                originalBitmap
            }

            try {
                val (format, outputMimeType) = when {
                    mimeType == "image/webp" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Bitmap.CompressFormat.WEBP_LOSSY to "image/webp"
                        } else {
                            @Suppress("DEPRECATION")
                            Bitmap.CompressFormat.WEBP to "image/webp"
                        }
                    }
                    else -> Bitmap.CompressFormat.JPEG to "image/jpeg"
                }

                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(format, quality, outputStream)
                val compressedBytes = outputStream.toByteArray()

                CompressedImage(
                    bytes = compressedBytes,
                    mimeType = outputMimeType,
                    originalSizeBytes = bytes.size.toLong(),
                    compressedSizeBytes = compressedBytes.size.toLong(),
                    widthPx = newWidth,
                    heightPx = newHeight
                )
            } finally {
                if (scaledBitmap !== originalBitmap) {
                    scaledBitmap.recycle()
                }
            }
        } finally {
            originalBitmap.recycle()
        }
    }

    private fun calculateScaledSize(
        width: Int, height: Int, maxWidth: Int, maxHeight: Int
    ): Pair<Int, Int> {
        if (width <= maxWidth && height <= maxHeight) return width to height
        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        return (width * ratio).toInt() to (height * ratio).toInt()
    }
}
