package com.dating.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.dating.auth.presentation.email_verification.EmailVerificationRoot
import com.dating.auth.presentation.forgot_password.ForgotPasswordRoot
import com.dating.auth.presentation.login.LoginRoot
import com.dating.auth.presentation.onboarding.OnboardingScreen
import com.dating.auth.presentation.register.RegisterRoot
import com.dating.auth.presentation.register.StepsRegisterRoot
import com.dating.auth.presentation.register_success.RegisterSuccessRoot
import com.dating.auth.presentation.reset_password.ResetPasswordRoot

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
) {
    navigation<AuthGraphRoutes.Graph>(
        startDestination = AuthGraphRoutes.Onboarding
    ) {
        composable<AuthGraphRoutes.Onboarding> {
            OnboardingScreen(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login)
                },
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoutes.Register)
                }
            )
        }
        composable<AuthGraphRoutes.Login> {
            LoginRoot(
                onLoginSuccess = onLoginSuccess,
                onForgotPasswordClick = {
                    navController.navigate(AuthGraphRoutes.ForgotPassword)
                },
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoutes.Register) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<AuthGraphRoutes.Register> {
            RegisterRoot(
                onNextClick = { email, password ->
                    navController.navigate(AuthGraphRoutes.StepsRegister(email, password))
                },
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo(AuthGraphRoutes.Register) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<AuthGraphRoutes.StepsRegister> {
            StepsRegisterRoot(
                onRegisterSuccess = { email ->
                    navController.navigate(AuthGraphRoutes.RegisterSuccess(email))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<AuthGraphRoutes.RegisterSuccess> {
            RegisterSuccessRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.RegisterSuccess> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<AuthGraphRoutes.EmailVerification>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "https://api.aura-safe-dating.com/api/auth/verify?token={token}"
                },
                navDeepLink {
                    this.uriPattern = "chirp://api.aura-safe-dating.com/api/auth/verify?token={token}"
                },
            )
        ) {
            EmailVerificationRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.EmailVerification> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onCloseClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.EmailVerification> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<AuthGraphRoutes.ForgotPassword> {
            ForgotPasswordRoot(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<AuthGraphRoutes.ResetPassword>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "https://api.aura-safe-dating.com/api/auth/reset-password?token={token}"
                },
                navDeepLink {
                    this.uriPattern = "chirp://api.aura-safe-dating.com/api/auth/reset-password?token={token}"
                },
            )
        ) {
            ResetPasswordRoot(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
