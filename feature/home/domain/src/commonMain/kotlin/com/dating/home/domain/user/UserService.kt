package com.dating.home.domain.user

import com.dating.core.domain.auth.User
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result

interface UserService {
    suspend fun updateLocation(latitude: Double, longitude: Double): Result<User, DataError.Remote>
    suspend fun getUserById(id: String): Result<User, DataError.Remote>
}
