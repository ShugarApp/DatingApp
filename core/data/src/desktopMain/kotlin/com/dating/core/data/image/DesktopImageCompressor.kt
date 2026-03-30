package com.dating.core.data.image

import com.dating.core.domain.image.CompressedImage
import com.dating.core.domain.image.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

class DesktopImageCompressor : ImageCompressor {

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

        val originalImage = ImageIO.read(ByteArrayInputStream(bytes))
            ?: return@withContext CompressedImage(
                bytes = bytes,
                mimeType = mimeType,
                originalSizeBytes = bytes.size.toLong(),
                compressedSizeBytes = bytes.size.toLong(),
                widthPx = 0,
                heightPx = 0
            )

        val (newWidth, newHeight) = calculateScaledSize(
            originalImage.width, originalImage.height, maxWidthPx, maxHeightPx
        )

        val scaledImage = if (newWidth != originalImage.width || newHeight != originalImage.height) {
            val tmp = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
            BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB).apply {
                createGraphics().apply {
                    drawImage(tmp, 0, 0, null)
                    dispose()
                }
            }
        } else {
            // Ensure TYPE_INT_RGB for JPEG encoding
            if (originalImage.type != BufferedImage.TYPE_INT_RGB) {
                BufferedImage(originalImage.width, originalImage.height, BufferedImage.TYPE_INT_RGB).apply {
                    createGraphics().apply {
                        drawImage(originalImage, 0, 0, null)
                        dispose()
                    }
                }
            } else {
                originalImage
            }
        }

        val outputStream = ByteArrayOutputStream()
        val writer = ImageIO.getImageWritersByFormatName("jpeg").next()
        val param = writer.defaultWriteParam.apply {
            compressionMode = ImageWriteParam.MODE_EXPLICIT
            compressionQuality = quality / 100f
        }

        writer.output = ImageIO.createImageOutputStream(outputStream)
        writer.write(null, IIOImage(scaledImage, null, null), param)
        writer.dispose()

        val compressedBytes = outputStream.toByteArray()

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
