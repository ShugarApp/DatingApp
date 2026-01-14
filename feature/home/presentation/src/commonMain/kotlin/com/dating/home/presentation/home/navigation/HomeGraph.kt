package com.dating.home.presentation.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dating.home.presentation.home.bottom_navigation.BottomNavigationContainer
import com.dating.home.presentation.profile.edit_profile.EditProfileScreen
import com.dating.home.presentation.profile.settings.SettingsScreen
import com.dating.home.presentation.detail.ProfileDetailScreen
import com.dating.home.presentation.chat.chat_detail.ChatDetailRoot
import com.dating.home.presentation.profile.verification.VerificationScreen

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    navigation<HomeGraphRoutes.Home>(
        startDestination = HomeGraphRoutes.BottomNavContainer(null)
    ) {
        composable<HomeGraphRoutes.ChatDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<HomeGraphRoutes.ChatDetailRoute>()
            ChatDetailRoot(
                chatId = route.chatId,
                onBack = { navController.popBackStack() },
                onChatMembersClick = {
                    // TODO: Handle this navigation if needed, or pass a callback
                }
            )
        }

        composable<HomeGraphRoutes.BottomNavContainer> { backStackEntry ->
            BottomNavigationContainer(
                onNavigateToProfile = { userId, imageUrl ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId, imageUrl))
                },
                onNavigateToChatDetail = { chatId ->
                    navController.navigate(HomeGraphRoutes.ChatDetailRoute(chatId))
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
                imageUrl = route.encodedImageUrl,
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
            VerificationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.SubscriptionRoute> {
            // Placeholder
        }
    }
}
