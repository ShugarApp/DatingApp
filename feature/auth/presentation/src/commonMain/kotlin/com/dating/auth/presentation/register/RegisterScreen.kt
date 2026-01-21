package com.dating.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.create_account
import aura.feature.auth.presentation.generated.resources.email
import aura.feature.auth.presentation.generated.resources.email_placeholder
import aura.feature.auth.presentation.generated.resources.login
import aura.feature.auth.presentation.generated.resources.next
import aura.feature.auth.presentation.generated.resources.password
import aura.feature.auth.presentation.generated.resources.password_hint
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.components.textfields.ChirpPasswordTextField
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterCredentialsViewModel = koinViewModel(),
    onNextClick: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterCredentialsEvent.OnNext -> {
                onNextClick(event.email, event.password)
            }

            RegisterCredentialsEvent.OnBack -> {
                onBackClick()
            }

            RegisterCredentialsEvent.OnLogin -> {
                onLoginClick()
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
    state: RegisterCredentialsState,
    onAction: (RegisterCredentialsAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AuthSnackbarScaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppCenterTopBar(
                    title = "",
                    containerColor = MaterialTheme.colorScheme.background,
                    onBack = { onAction(RegisterCredentialsAction.OnBackClick) }
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            AppBrandLogo(modifier = Modifier.size(80.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.create_account),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChirpTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                onFocusChanged = {
                    onAction(RegisterCredentialsAction.OnInputTextFocusGain)
                },
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChirpPasswordTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString() ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                onFocusChanged = {
                    onAction(RegisterCredentialsAction.OnInputTextFocusGain)
                },
                onToggleVisibilityClick = {
                    onAction(RegisterCredentialsAction.OnTogglePasswordVisibilityClick)
                },
                isPasswordVisible = state.isPasswordVisible,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            ChirpButton(
                text = stringResource(Res.string.next),
                onClick = { onAction(RegisterCredentialsAction.OnNextClick) },
                enabled = state.canProceed,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ChirpButton(
                text = stringResource(Res.string.login),
                onClick = { onAction(RegisterCredentialsAction.OnLoginClick) },
                style = AppButtonStyle.TEXT,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewCredentials() {
    AppTheme {
        RegisterScreen(
            state = RegisterCredentialsState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}