package com.dating.aura.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.dating.auth.presentation.navigation.AuthGraphRoutes
import com.dating.auth.presentation.navigation.authGraph
import com.dating.home.presentation.navigation.HomeGraphRoutes
import com.dating.home.presentation.navigation.homeGraph

@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(HomeGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        homeGraph(
            navController = navController,
            onLogout = {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(HomeGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
    }
}