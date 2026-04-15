package com.dating.auth.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.create_account
import aura.feature.auth.presentation.generated.resources.login
import aura.feature.auth.presentation.generated.resources.welcome_to_chirp
import aura.feature.auth.presentation.generated.resources.onboarding_body
import aura.feature.auth.presentation.generated.resources.onboarding_terms
import com.dating.auth.presentation.google.GoogleSignInButton
import com.dating.auth.presentation.google.rememberGoogleSignInLauncher
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingRoot(
    viewModel: OnboardingViewModel = koinViewModel(),
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onGoogleSuccess: () -> Unit,
    onGoogleNewUser: (email: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            OnboardingEvent.GoogleSuccess -> onGoogleSuccess()
            is OnboardingEvent.GoogleNewUser -> onGoogleNewUser(event.email)
        }
    }

    val launchGoogleSignIn = rememberGoogleSignInLauncher(
        onIdTokenReceived = { idToken ->
            viewModel.onAction(OnboardingAction.OnGoogleIdTokenReceived(idToken))
        },
        onError = {
            viewModel.onAction(OnboardingAction.OnGoogleSignInError)
        }
    )

    OnboardingScreen(
        onLoginClick = onLoginClick,
        onCreateAccountClick = onCreateAccountClick,
        onGoogleSignInClick = launchGoogleSignIn,
        isGoogleLoading = state.isGoogleLoading
    )
}

@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onGoogleSignInClick: () -> Unit = {},
    isGoogleLoading: Boolean = false
) {
    AuthSnackbarScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            0.4f to Color.Transparent,
                            1.0f to MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                AppBrandLogo(modifier = Modifier.size(96.dp))

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(Res.string.welcome_to_chirp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.onboarding_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                ChirpButton(
                    text = stringResource(Res.string.login),
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ChirpButton(
                    text = stringResource(Res.string.create_account),
                    onClick = onCreateAccountClick,
                    style = AppButtonStyle.TEXT,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "o",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.extended.textSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                GoogleSignInButton(
                    onClick = onGoogleSignInClick,
                    isLoading = isGoogleLoading,
                    enabled = !isGoogleLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.onboarding_terms),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 64.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        OnboardingScreen(
            onLoginClick = {},
            onCreateAccountClick = {}
        )
    }
}
