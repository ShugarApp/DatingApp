package com.dating.home.domain.message

import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.home.domain.models.MediaUploadInfo

interface ChatMediaService {
    suspend fun getUploadUrl(
        chatId: String,
        mimeType: String
    ): Result<MediaUploadInfo, DataError.Remote>

    suspend fun uploadMedia(
        uploadUrl: String,
        headers: Map<String, String>,
        data: ByteArray
    ): EmptyResult<DataError.Remote>
}
