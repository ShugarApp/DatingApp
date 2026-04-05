package com.dating.home.presentation.home.navigation

import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable
    data object Home : HomeGraphRoutes

    @Serializable
    data class BottomNavContainer(val chatId: String? = null, val section: String? = null) : HomeGraphRoutes

    @Serializable
    data class ProfileDetailRoute(val userId: String, val encodedImageUrl: String? = null, val isOwnProfile: Boolean = false, val isMatch: Boolean = false) : HomeGraphRoutes

    @Serializable
    data class ChatDetailRoute(val chatId: String? = null) : HomeGraphRoutes

    @Serializable
    data object EditProfileRoute : HomeGraphRoutes

    @Serializable
    data object SettingsRoute : HomeGraphRoutes

    @Serializable
    data object VerificationRoute : HomeGraphRoutes
    
    @Serializable
    data object ChangePasswordRoute : HomeGraphRoutes

    @Serializable
    data object SubscriptionRoute : HomeGraphRoutes

    @Serializable
    data object PauseAccountRoute : HomeGraphRoutes

    @Serializable
    data object IncognitoModeRoute : HomeGraphRoutes

    @Serializable
    data object DeleteAccountRoute : HomeGraphRoutes

    @Serializable
    data object BlockedUsersRoute : HomeGraphRoutes

    @Serializable
    data object EmergencyOnboardingRoute : HomeGraphRoutes

    @Serializable
    data object EmergencyContactsRoute : HomeGraphRoutes
}
