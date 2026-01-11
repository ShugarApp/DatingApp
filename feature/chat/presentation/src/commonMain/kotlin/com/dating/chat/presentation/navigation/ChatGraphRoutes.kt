package com.dating.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dating.chat.presentation.bottom_navigation.BottomNavigationContainer
import com.dating.chat.presentation.profile_detail.ProfileDetailScreen
import kotlinx.serialization.Serializable

sealed interface ChatGraphRoutes {
    @Serializable
    data object Graph : ChatGraphRoutes

    @Serializable
    data class BottomNavContainer(val chatId: String? = null) : ChatGraphRoutes

    @Serializable
    data object FeedRoute : ChatGraphRoutes

    @Serializable
    data object MatchesRoute : ChatGraphRoutes

    @Serializable
    data object MessagesRoute : ChatGraphRoutes

    @Serializable
    data class ProfileDetailRoute(val userId: String) : ChatGraphRoutes

    @Serializable
    data class ChatListDetailRoute(val chatId: String? = null) : ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.BottomNavContainer(null)
        //startDestination = ChatGraphRoutes.ChatListDetailRoute(null)
    ) {
        /*
        composable<ChatGraphRoutes.ChatListDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ChatGraphRoutes.ChatListDetailRoute>()
            ChatListDetailAdaptiveLayout(
                initialChatId = route.chatId,
                onLogout = onLogout
            )
        }
        */

        composable<ChatGraphRoutes.BottomNavContainer>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStackEntry ->
            BottomNavigationContainer(
                onLogout = onLogout,
                onNavigateToProfile = { userId ->
                    navController.navigate(ChatGraphRoutes.ProfileDetailRoute(userId))
                }
            )
        }

        composable<ChatGraphRoutes.ProfileDetailRoute> { backStackEntry ->
            val route: ChatGraphRoutes.ProfileDetailRoute = backStackEntry.toRoute()
            ProfileDetailScreen(
                userId = route.userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}