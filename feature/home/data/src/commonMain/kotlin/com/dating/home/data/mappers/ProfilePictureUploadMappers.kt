package com.dating.home.data.mappers

import com.dating.home.data.dto.response.ProfilePictureUploadUrlsResponse
import com.dating.home.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}
