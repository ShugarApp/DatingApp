package com.dating.home.domain.user

import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result

interface UserService {
    suspend fun getMyProfile(): Result<User, DataError.Remote>
    suspend fun updateProfile(
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
    ): Result<User, DataError.Remote>
    // Returns the confirmed publicUrl so the caller can update state locally
    suspend fun uploadPhoto(imageBytes: ByteArray, mimeType: String, index: Int): Result<String, DataError.Remote>
    suspend fun deletePhoto(index: Int): EmptyResult<DataError.Remote>
    suspend fun reorderPhotos(photos: List<String>): EmptyResult<DataError.Remote>
    suspend fun updateLocation(latitude: Double, longitude: Double): Result<User, DataError.Remote>
    suspend fun getUserById(id: String): Result<User, DataError.Remote>
    suspend fun deleteAccount(): EmptyResult<DataError.Remote>
    suspend fun pauseAccount(pause: Boolean): Result<User, DataError.Remote>
    suspend fun toggleIncognitoMode(incognito: Boolean): Result<User, DataError.Remote>
}
