package com.dating.home.presentation.home.navigation

import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable
    data object Home : HomeGraphRoutes

    @Serializable
    data class BottomNavContainer(val chatId: String? = null) : HomeGraphRoutes

    @Serializable
    data class ProfileDetailRoute(val userId: String, val encodedImageUrl: String? = null) : HomeGraphRoutes

    @Serializable
    data class ChatDetailRoute(val chatId: String? = null) : HomeGraphRoutes

    @Serializable
    data object EditProfileRoute : HomeGraphRoutes

    @Serializable
    data object SettingsRoute : HomeGraphRoutes

    @Serializable
    data object VerificationRoute : HomeGraphRoutes

    @Serializable
    data object SubscriptionRoute : HomeGraphRoutes
}
