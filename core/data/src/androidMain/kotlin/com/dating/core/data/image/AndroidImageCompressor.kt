package com.dating.core.data.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import com.dating.core.domain.image.CompressedImage
import com.dating.core.domain.image.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
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

        val decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = 0,
                heightPx = 0
            )

        val exifOrientation = ByteArrayInputStream(bytes).use { stream ->
            ExifInterface(stream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        }
        val originalBitmap = applyExifRotation(decodedBitmap, exifOrientation)
        if (originalBitmap !== decodedBitmap) decodedBitmap.recycle()

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

    private fun applyExifRotation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.postRotate(90f); matrix.postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.postRotate(-90f); matrix.postScale(-1f, 1f) }
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun calculateScaledSize(
        width: Int, height: Int, maxWidth: Int, maxHeight: Int
    ): Pair<Int, Int> {
        if (width <= maxWidth && height <= maxHeight) return width to height
        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        return (width * ratio).toInt() to (height * ratio).toInt()
    }
}
