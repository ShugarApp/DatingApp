package com.dating.home.presentation.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dating.home.presentation.home.bottom_navigation.BottomNavigationContainer
import com.dating.home.presentation.profile.edit_profile.EditProfileScreen
import com.dating.home.presentation.profile.settings.DeleteAccountScreen
import com.dating.home.presentation.profile.settings.IncognitoModeScreen
import com.dating.home.presentation.profile.settings.PauseAccountScreen
import com.dating.home.presentation.profile.settings.SettingsScreen
import com.dating.home.presentation.profile.settings.blocked.BlockedUsersScreen
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
                onProfileClick = { userId ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId, isMatch = true))
                }
            )
        }

        composable<HomeGraphRoutes.BottomNavContainer> { backStackEntry ->
            val swipedUserId = backStackEntry.savedStateHandle.get<String>("swiped_user_id")
            val swipedIsDislike = backStackEntry.savedStateHandle.get<Boolean>("swiped_is_dislike") ?: false
            val blockedUserId = backStackEntry.savedStateHandle.get<String>("blocked_user_id")
            LaunchedEffect(swipedUserId) {
                if (swipedUserId != null) {
                    backStackEntry.savedStateHandle.remove<String>("swiped_user_id")
                    backStackEntry.savedStateHandle.remove<Boolean>("swiped_is_dislike")
                }
            }
            LaunchedEffect(blockedUserId) {
                if (blockedUserId != null) {
                    backStackEntry.savedStateHandle.remove<String>("blocked_user_id")
                }
            }
            BottomNavigationContainer(
                swipedUserId = swipedUserId,
                swipedIsDislike = swipedIsDislike,
                blockedUserId = blockedUserId,
                onNavigateToProfile = { userId, imageUrl ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId, imageUrl))
                },
                onNavigateToMatchProfile = { userId, imageUrl ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId, imageUrl, isMatch = true))
                },
                onNavigateToOwnProfile = { userId, imageUrl ->
                    navController.navigate(HomeGraphRoutes.ProfileDetailRoute(userId, imageUrl, isOwnProfile = true))
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
                isOwnProfile = route.isOwnProfile,
                isMatch = route.isMatch,
                onBack = { navController.popBackStack() },
                onSwipedUser = { swipedUserId, isDislike ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("swiped_user_id", swipedUserId)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("swiped_is_dislike", isDislike)
                },
                onUserBlocked = { blockedId ->
                    navController.getBackStackEntry<HomeGraphRoutes.BottomNavContainer>()
                        .savedStateHandle
                        .set("blocked_user_id", blockedId)
                }
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
                onLogout = onLogout,
                onChangePassword = {
                    navController.navigate(HomeGraphRoutes.ChangePasswordRoute)
                },
                onDeleteAccount = {
                    navController.navigate(HomeGraphRoutes.DeleteAccountRoute)
                },
                onPauseAccount = {
                    navController.navigate(HomeGraphRoutes.PauseAccountRoute)
                },
                onBlockedUsers = {
                    navController.navigate(HomeGraphRoutes.BlockedUsersRoute)
                },
                onIncognitoMode = {
                    navController.navigate(HomeGraphRoutes.IncognitoModeRoute)
                }
            )
        }

        composable<HomeGraphRoutes.PauseAccountRoute> {
            PauseAccountScreen(
                onBack = { navController.popBackStack() },
                onNavigateToFeed = {
                    navController.navigate(HomeGraphRoutes.BottomNavContainer(null)) {
                        popUpTo(HomeGraphRoutes.BottomNavContainer(null)) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeGraphRoutes.IncognitoModeRoute> {
            IncognitoModeScreen(
                onBack = { navController.popBackStack() },
                onNavigateToFeed = {
                    navController.navigate(HomeGraphRoutes.BottomNavContainer(null)) {
                        popUpTo(HomeGraphRoutes.BottomNavContainer(null)) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeGraphRoutes.DeleteAccountRoute> {
            DeleteAccountScreen(
                onBack = { navController.popBackStack() },
                onDeleteAccount = onLogout
            )
        }

        composable<HomeGraphRoutes.BlockedUsersRoute> {
            BlockedUsersScreen(
                onBack = { navController.popBackStack() }
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

        composable<HomeGraphRoutes.ChangePasswordRoute> {
            com.dating.home.presentation.profile.settings.changepassword.ChangePasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
