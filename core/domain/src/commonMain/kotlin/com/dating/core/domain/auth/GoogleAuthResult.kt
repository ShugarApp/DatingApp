package com.dating.core.domain.auth

data class GoogleAuthResult(
    val authInfo: AuthInfo?,
    val isNewUser: Boolean
)
