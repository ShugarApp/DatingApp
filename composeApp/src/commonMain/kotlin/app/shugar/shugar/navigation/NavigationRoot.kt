package app.shugar.shugar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dating.aura.analytics.AnalyticsService
import com.dating.auth.presentation.navigation.AuthGraphRoutes
import com.dating.auth.presentation.navigation.authGraph
import com.dating.home.presentation.home.navigation.HomeGraphRoutes
import com.dating.home.presentation.home.navigation.homeGraph
import org.koin.compose.koinInject

@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any,
    analyticsService: AnalyticsService = koinInject()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.destination?.route?.let { route ->
            val screenName = route
                .substringAfterLast(".")
                .substringBefore("/")
                .substringBefore("?")
                .ifBlank { route }
            analyticsService.trackScreenView(screenName)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(HomeGraphRoutes.Home) {
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
                    popUpTo(HomeGraphRoutes.Home) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
