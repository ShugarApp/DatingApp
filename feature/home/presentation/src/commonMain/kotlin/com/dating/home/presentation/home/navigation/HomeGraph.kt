package com.dating.home.presentation.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dating.home.presentation.home.bottom_navigation.BottomNavSection
import com.dating.home.presentation.home.bottom_navigation.BottomNavigationContainer
import com.dating.home.presentation.profile.edit_profile.EditProfileScreen
import com.dating.home.presentation.emergency.contacts.EmergencyContactsScreen
import com.dating.home.presentation.emergency.onboarding.EmergencyOnboardingScreen
import com.dating.home.presentation.profile.settings.SafeDateScreen
import com.dating.home.presentation.profile.settings.SafetyTipsScreen
import com.dating.home.presentation.profile.settings.DateSafetyChecklistScreen
import com.dating.home.presentation.profile.settings.DiscoverySettingsScreen
import com.dating.home.presentation.profile.settings.LegalAccountScreen
import com.dating.home.presentation.profile.settings.PrivacySettingsScreen
import com.dating.home.presentation.profile.settings.SafetyCenterScreen
import com.dating.home.presentation.profile.settings.SecuritySettingsScreen
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

        composable<HomeGraphRoutes.BottomNavContainer>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "chirp://home?section={section}" }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<HomeGraphRoutes.BottomNavContainer>()
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
                initialSection = when (route.section) {
                    "matches" -> BottomNavSection.MATCHES
                    else -> BottomNavSection.FEED
                },
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
                },
                onSafetyCenter = {
                    navController.navigate(HomeGraphRoutes.SafetyCenterRoute)
                },
                onDateSafetyTips = {
                    navController.navigate(HomeGraphRoutes.DateSafetyTipsRoute)
                },
                onDateSafetyChecklist = {
                    navController.navigate(HomeGraphRoutes.DateSafetyChecklistRoute)
                },
                onSafeDate = {
                    navController.navigate(HomeGraphRoutes.SafeDateRoute)
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
                onPauseAccount = {
                    navController.navigate(HomeGraphRoutes.PauseAccountRoute)
                },
                onIncognitoMode = {
                    navController.navigate(HomeGraphRoutes.IncognitoModeRoute)
                },
                onSafetyCenter = {
                    navController.navigate(HomeGraphRoutes.SafetyCenterRoute)
                },
                onDiscoverySettings = {
                    navController.navigate(HomeGraphRoutes.DiscoverySettingsRoute)
                },
                onLegalAccount = {
                    navController.navigate(HomeGraphRoutes.LegalAccountRoute)
                },
                onSecuritySettings = {
                    navController.navigate(HomeGraphRoutes.SecuritySettingsRoute)
                },
                onPrivacySettings = {
                    navController.navigate(HomeGraphRoutes.PrivacySettingsRoute)
                }
            )
        }

        composable<HomeGraphRoutes.SecuritySettingsRoute> {
            SecuritySettingsScreen(
                onBack = { navController.popBackStack() },
                onChangePassword = {
                    navController.navigate(HomeGraphRoutes.ChangePasswordRoute)
                },
                onDeleteAccount = {
                    navController.navigate(HomeGraphRoutes.DeleteAccountRoute)
                }
            )
        }

        composable<HomeGraphRoutes.PrivacySettingsRoute> {
            PrivacySettingsScreen(
                onBack = { navController.popBackStack() },
                onBlockedUsers = {
                    navController.navigate(HomeGraphRoutes.BlockedUsersRoute)
                }
            )
        }

        composable<HomeGraphRoutes.DiscoverySettingsRoute> {
            DiscoverySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.LegalAccountRoute> {
            LegalAccountScreen(
                onBack = { navController.popBackStack() }
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

        composable<HomeGraphRoutes.EmergencyOnboardingRoute> {
            EmergencyOnboardingScreen(
                onFinished = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.EmergencyContactsRoute> {
            EmergencyContactsScreen(
                onBack = { navController.popBackStack() },
                onCall911 = {
                    // Launch phone intent via platform-specific mechanism
                    // On Android this will be handled via Intent in the Activity
                }
            )
        }

        composable<HomeGraphRoutes.DateSafetyTipsRoute> {
            SafetyTipsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.DateSafetyChecklistRoute> {
            DateSafetyChecklistScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<HomeGraphRoutes.SafetyCenterRoute> {
            SafetyCenterScreen(
                onBack = { navController.popBackStack() },
                onEmergencyContacts = {
                    navController.navigate(HomeGraphRoutes.EmergencyContactsRoute)
                },
                onEmergencyTutorial = {
                    navController.navigate(HomeGraphRoutes.EmergencyOnboardingRoute)
                },
                onDateSafetyTips = {
                    navController.navigate(HomeGraphRoutes.DateSafetyTipsRoute)
                },
                onDateSafetyChecklist = {
                    navController.navigate(HomeGraphRoutes.DateSafetyChecklistRoute)
                },
                onSafeDate = {
                    navController.navigate(HomeGraphRoutes.SafeDateRoute)
                }
            )
        }

        composable<HomeGraphRoutes.SafeDateRoute> {
            SafeDateScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
