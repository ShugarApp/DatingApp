package com.dating.core.data.auth

import com.dating.core.data.dto.AuthInfoSerializable
import com.dating.core.data.dto.GoogleAuthInfoSerializable
import com.dating.core.data.dto.requests.ChangePasswordRequest
import com.dating.core.data.dto.requests.CompleteProfileRequest
import com.dating.core.data.dto.requests.EmailRequest
import com.dating.core.data.dto.requests.GoogleLoginRequest
import com.dating.core.data.dto.requests.GoogleRegisterRequest
import com.dating.core.data.dto.requests.LoginRequest
import com.dating.core.data.dto.requests.RefreshRequest
import com.dating.core.data.dto.requests.RegisterRequest
import com.dating.core.data.dto.requests.ResetPasswordRequest
import com.dating.core.data.mappers.toDomain
import com.dating.core.data.networking.get
import com.dating.core.data.networking.post
import com.dating.core.data.networking.put
import com.dating.core.domain.auth.AuthInfo
import com.dating.core.domain.auth.AuthService
import com.dating.core.domain.auth.GoogleAuthResult
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.EmptyResult
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

class KtorAuthService(private val httpClient: HttpClient) : AuthService {

    override suspend fun checkEmailAvailability(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/check-email",
            body = EmailRequest(email)
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoSerializable>(
            route = "/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map { authInfoSerializable ->
            authInfoSerializable.toDomain()
        }
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): Result<GoogleAuthResult, DataError.Remote> {
        return httpClient.post<GoogleLoginRequest, GoogleAuthInfoSerializable>(
            route = "/auth/google",
            body = GoogleLoginRequest(idToken = idToken)
        ).map { it.toDomain() }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String,
        birthDate: String?,
        gender: String?,
        interestedIn: String?,
        lookingFor: String?,
        idealDate: String?
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password,
                birthDate = birthDate,
                gender = gender,
                interestedIn = interestedIn,
                lookingFor = lookingFor,
                idealDate = idealDate
            )
        )
    }

    override suspend fun registerWithGoogle(
        idToken: String,
        username: String,
        birthDate: String,
        gender: String,
        interestedIn: String,
        lookingFor: String,
        idealDate: String?
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<GoogleRegisterRequest, AuthInfoSerializable>(
            route = "/auth/register/google",
            body = GoogleRegisterRequest(
                idToken = idToken,
                username = username,
                birthDate = birthDate,
                gender = gender,
                interestedIn = interestedIn,
                lookingFor = lookingFor,
                idealDate = idealDate
            )
        ).map { it.toDomain() }
    }

    override suspend fun completeProfile(
        username: String,
        birthDate: String,
        gender: String,
        interestedIn: String,
        lookingFor: String,
        idealDate: String?
    ): EmptyResult<DataError.Remote> {
        return httpClient.put(
            route = "/users/profile",
            body = CompleteProfileRequest(
                username = username,
                birthDate = birthDate,
                gender = gender,
                interestedIn = interestedIn,
                lookingFor = lookingFor,
                idealDate = idealDate
            )
        )
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(email),
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/auth/verify",
            queryParams = mapOf("token" to token)
        )
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post<EmailRequest, Unit>(
            route = "/auth/forgot-password",
            body = EmailRequest(email)
        )
    }

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/reset-password",
            body = ResetPasswordRequest(
                newPassword = newPassword,
                token = token
            )
        )
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/change-password",
            body = ChangePasswordRequest(
                oldPassword = currentPassword,
                newPassword = newPassword
            )
        )
    }

    override suspend fun logout(refreshToken: String): EmptyResult<DataError.Remote> {
        return httpClient.post<RefreshRequest, Unit>(
            route = "/auth/logout",
            body = RefreshRequest(refreshToken)
        ).onSuccess {
            httpClient.authProvider<BearerAuthProvider>()?.clearToken()
        }
    }
}
