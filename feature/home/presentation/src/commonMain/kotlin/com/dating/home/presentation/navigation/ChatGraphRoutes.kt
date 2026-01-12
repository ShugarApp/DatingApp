package com.dating.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dating.home.presentation.bottom_navigation.BottomNavigationContainer
import com.dating.home.presentation.profile.edit_profile.EditProfileScreen
import com.dating.home.presentation.profile.settings.SettingsScreen
import com.dating.home.presentation.profile_detail.ProfileDetailScreen
import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable
    data object Graph : HomeGraphRoutes

    @Serializable
    data class BottomNavContainer(val chatId: String? = null) : HomeGraphRoutes

    @Serializable
    data object FeedRoute : HomeGraphRoutes

    @Serializable
    data object MatchesRoute : HomeGraphRoutes

    @Serializable
    data object MessagesRoute : HomeGraphRoutes

    @Serializable
    data class ProfileDetailRoute(val userId: String) : HomeGraphRoutes

    @Serializable
    data class ChatListDetailRoute(val chatId: String? = null) : HomeGraphRoutes

    @Serializable
    data object EditProfileRoute : HomeGraphRoutes

    @Serializable
    data object SettingsRoute : HomeGraphRoutes

    @Serializable
    data object VerificationRoute : HomeGraphRoutes

    @Serializable
    data object SubscriptionRoute : HomeGraphRoutes
}

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    navigation<HomeGraphRoutes.Graph>(
        startDestination = HomeGraphRoutes.BottomNavContainer(null)
        //startDestination = HomeGraphRoutes.ChatListDetailRoute(null)
    ) {
        /*
        composable<HomeGraphRoutes.ChatListDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<HomeGraphRoutes.ChatListDetailRoute>()
            ChatListDetailAdaptiveLayout(
                initialChatId = route.chatId,
                onLogout = onLogout
            )
        }
        */

        composable<HomeGraphRoutes.BottomNavContainer>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            BottomNavigationContainer(
                onLogout = onLogout,
                onNavigateToProfile = { userId ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId))
                },
                onEditProfile = {
                    navController.navigate(HomeGraphRoutes.EditProfileRoute)
                },
                onSettings = {
                    navController.navigate(HomeGraphRoutes.SettingsRoute)
                },
                onVerification = {
                    navController.navigate(HomeGraphRoutes.VerificationRoute)
                },
                onSubscriptions = {
                    navController.navigate(HomeGraphRoutes.SubscriptionRoute)
                }
            )
        }

        composable<HomeGraphRoutes.ProfileDetailRoute> { backStackEntry ->
            val route: HomeGraphRoutes.ProfileDetailRoute = backStackEntry.toRoute()
            ProfileDetailScreen(
                userId = route.userId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.EditProfileRoute> {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        
        composable<HomeGraphRoutes.SettingsRoute> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = onLogout
            )
        }
        
        composable<HomeGraphRoutes.VerificationRoute> {
            // Placeholder
        }
        
        composable<HomeGraphRoutes.SubscriptionRoute> {
            // Placeholder
        }
    }
}