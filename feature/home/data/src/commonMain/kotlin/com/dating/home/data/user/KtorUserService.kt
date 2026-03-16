package com.dating.home.data.user

import com.dating.core.data.dto.UserSerializable
import com.dating.core.data.mappers.toDomain
import com.dating.core.data.networking.delete
import com.dating.core.data.networking.get
import com.dating.core.data.networking.post
import com.dating.core.data.networking.put
import com.dating.core.data.networking.safeCall
import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.home.data.dto.request.ConfirmPhotoRequest
import com.dating.home.data.dto.request.LocationRequest
import com.dating.home.data.dto.request.PauseAccountRequest
import com.dating.home.data.dto.request.ReorderPhotosRequest
import com.dating.home.data.dto.request.UpdateProfileRequest
import com.dating.home.data.dto.response.ProfilePictureUploadUrlsResponse
import com.dating.home.domain.user.UserService
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class KtorUserService(private val httpClient: HttpClient) : UserService {

    override suspend fun getMyProfile(): Result<User, DataError.Remote> {
        return httpClient.get<UserSerializable>(
            route = "/users/profile"
        ).map { it.toDomain() }
    }

    override suspend fun updateProfile(
        bio: String?,
        gender: String?,
        birthDate: String?,
        jobTitle: String?,
        company: String?,
        education: String?,
        height: Int?,
        zodiac: String?,
        smoking: String?,
        drinking: String?,
        interests: List<String>?
    ): Result<User, DataError.Remote> {
        return httpClient.put<UpdateProfileRequest, UserSerializable>(
            route = "/users/profile",
            body = UpdateProfileRequest(
                bio = bio,
                gender = gender,
                birthDate = birthDate,
                jobTitle = jobTitle,
                company = company,
                education = education,
                height = height,
                zodiac = zodiac,
                smoking = smoking,
                drinking = drinking,
                interests = interests
            )
        ).map { it.toDomain() }
    }

    // Step 1: get signed upload URL. Step 2: upload bytes. Step 3: confirm (204).
    // Returns the confirmed publicUrl so the ViewModel can update state locally.
    override suspend fun uploadPhoto(
        imageBytes: ByteArray,
        mimeType: String,
        index: Int
    ): Result<String, DataError.Remote> {
        val urlResult = httpClient.post<Unit, ProfilePictureUploadUrlsResponse>(
            route = "/users/profile/photos/upload-url",
            queryParams = mapOf("mimeType" to mimeType),
            body = Unit
        )
        if (urlResult is Result.Failure) return urlResult

        val urls = (urlResult as Result.Success).data
        val uploadResult = safeCall<Unit> {
            httpClient.put {
                url(urls.uploadUrl)
                urls.headers.forEach { (key, value) -> headers[key] = value }
                setBody(imageBytes)
            }
        }
        if (uploadResult is Result.Failure) return uploadResult

        val confirmResult = httpClient.post<ConfirmPhotoRequest, Unit>(
            route = "/users/profile/photos/confirm",
            body = ConfirmPhotoRequest(publicUrl = urls.publicUrl, index = index)
        )
        if (confirmResult is Result.Failure) return confirmResult

        return Result.Success(urls.publicUrl)
    }

    override suspend fun deletePhoto(index: Int): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(route = "/users/profile/photos/$index")
    }

    override suspend fun reorderPhotos(photos: List<String>): EmptyResult<DataError.Remote> {
        return httpClient.put<ReorderPhotosRequest, Unit>(
            route = "/users/profile/photos/reorder",
            body = ReorderPhotosRequest(photos = photos)
        )
    }

    override suspend fun updateLocation(
        latitude: Double,
        longitude: Double
    ): Result<User, DataError.Remote> {
        return httpClient.put<LocationRequest, UserSerializable>(
            route = "/users/profile/location",
            body = LocationRequest(latitude = latitude, longitude = longitude)
        ).map { it.toDomain() }
    }

    override suspend fun getUserById(id: String): Result<User, DataError.Remote> {
        return httpClient.get<UserSerializable>(
            route = "/users/$id"
        ).map { it.toDomain() }
    }

    override suspend fun deleteAccount(): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(route = "/users/profile")
    }

    override suspend fun pauseAccount(pause: Boolean): Result<User, DataError.Remote> {
        return httpClient.put<PauseAccountRequest, UserSerializable>(
            route = "/users/profile/pause",
            body = PauseAccountRequest(pause = pause)
        ).map { it.toDomain() }
    }
}
