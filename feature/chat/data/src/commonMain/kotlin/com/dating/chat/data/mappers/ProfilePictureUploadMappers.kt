package com.dating.chat.data.mappers

import com.dating.chat.data.dto.response.ProfilePictureUploadUrlsResponse
import com.dating.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}