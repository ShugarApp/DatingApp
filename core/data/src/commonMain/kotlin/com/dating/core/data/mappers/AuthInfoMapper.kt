package com.dating.core.data.mappers

import com.dating.core.data.dto.AuthInfoSerializable
import com.dating.core.data.dto.UserSerializable
import com.dating.core.domain.auth.AuthInfo
import com.dating.core.domain.auth.User
import com.dating.core.domain.auth.UserStatus

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
        isPaused = isPaused,
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
        interests = interests
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail,
        status = status.name,
        isPaused = isPaused,
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
        interests = interests
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable()
    )
}
