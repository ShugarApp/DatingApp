package com.dating.core.data.image

import com.dating.core.domain.image.CompressedImage
import com.dating.core.domain.image.ImageCompressor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

class IosImageCompressor : ImageCompressor {

    @OptIn(ExperimentalForeignApi::class)
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

        val nsData = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }

        val originalImage = UIImage(data = nsData)
            ?: return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = 0,
                heightPx = 0
            )

        val originalWidth: Int
        val originalHeight: Int
        originalImage.size.useContents {
            originalWidth = width.toInt()
            originalHeight = height.toInt()
        }

        val (newWidth, newHeight) = calculateScaledSize(
            originalWidth, originalHeight, maxWidthPx, maxHeightPx
        )

        // Always redraw into a context so EXIF orientation is normalized to UIImageOrientationUp
        val resizedImage = run {
            val newSize = CGSizeMake(newWidth.toDouble(), newHeight.toDouble())
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            originalImage.drawInRect(CGRectMake(0.0, 0.0, newWidth.toDouble(), newHeight.toDouble()))
            val result = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            result ?: originalImage
        }

        val compressionQuality = quality / 100.0
        val compressedData = UIImageJPEGRepresentation(resizedImage, compressionQuality)
            ?: return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = originalWidth,
                heightPx = originalHeight
            )

        val compressedBytes = ByteArray(compressedData.length.toInt()).also { array ->
            array.usePinned { pinned ->
                memcpy(pinned.addressOf(0), compressedData.bytes, compressedData.length)
            }
        }

        CompressedImage(
            bytes = compressedBytes,
            mimeType = "image/jpeg",
            originalSizeBytes = bytes.size.toLong(),
            compressedSizeBytes = compressedBytes.size.toLong(),
            widthPx = newWidth,
            heightPx = newHeight
        )
    }

    private fun calculateScaledSize(
        width: Int, height: Int, maxWidth: Int, maxHeight: Int
    ): Pair<Int, Int> {
        if (width <= maxWidth && height <= maxHeight) return width to height
        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        return (width * ratio).toInt() to (height * ratio).toInt()
    }
}
