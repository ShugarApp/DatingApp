package com.dating.core.data.mappers

import com.dating.core.data.dto.AuthInfoSerializable
import com.dating.core.data.dto.GoogleAuthInfoSerializable
import com.dating.core.data.dto.UserSerializable
import com.dating.core.data.dto.ProfileVerificationSerializable
import com.dating.core.domain.auth.AuthInfo
import com.dating.core.domain.auth.GoogleAuthResult
import com.dating.core.domain.auth.ProfileVerification
import com.dating.core.domain.auth.User
import com.dating.core.domain.auth.UserStatus
import com.dating.core.domain.auth.VerificationStatus

fun AuthInfoSerializable.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain()
    )
}

fun UserSerializable.toDomain(): User {
    return User(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        status = try { UserStatus.valueOf(status) } catch (_: Exception) { UserStatus.PENDING },
        verificationStatus = try { VerificationStatus.valueOf(verificationStatus) } catch (_: Exception) { VerificationStatus.UNVERIFIED },
        isPaused = isPaused,
        isIncognito = isIncognito,
        photos = photos,
        city = city,
        country = country,
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
        interests = interests,
        idealDate = idealDate,
        interestedIn = interestedIn,
        lookingFor = lookingFor
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        status = status.name,
        verificationStatus = verificationStatus.name,
        isPaused = isPaused,
        isIncognito = isIncognito,
        photos = photos,
        city = city,
        country = country,
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
        interests = interests,
        idealDate = idealDate,
        interestedIn = interestedIn,
        lookingFor = lookingFor
    )
}

fun GoogleAuthInfoSerializable.toDomain(): GoogleAuthResult {
    return GoogleAuthResult(
        authInfo = AuthInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toDomain()
        ),
        isNewUser = isNewUser
    )
}

fun ProfileVerificationSerializable.toDomain(): ProfileVerification {
    return ProfileVerification(
        id = id,
        status = try { VerificationStatus.valueOf(status) } catch (_: Exception) { VerificationStatus.PENDING },
        rejectionReason = rejectionReason,
        faceConfidence = faceConfidence,
        matchScore = matchScore,
        attemptNumber = attemptNumber,
        createdAt = createdAt
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable()
    )
}
