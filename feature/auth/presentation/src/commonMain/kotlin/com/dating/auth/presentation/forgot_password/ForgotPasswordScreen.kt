package com.dating.auth.presentation.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.email
import aura.feature.auth.presentation.generated.resources.email_placeholder
import aura.feature.auth.presentation.generated.resources.forgot_password
import aura.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import aura.feature.auth.presentation.generated.resources.submit
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    onBackClick: () -> Unit
) {
    AuthSnackbarScaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                AppBrandLogo(
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = stringResource(Res.string.forgot_password),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (state.errorText != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.errorText.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Inputs
                ChirpTextField(
                    state = state.emailTextFieldState,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(Res.string.email_placeholder),
                    title = stringResource(Res.string.email),
                    isError = state.errorText != null,
                    supportingText = state.errorText?.asString(),
                    keyboardType = KeyboardType.Email,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Button
                ChirpButton(
                    text = stringResource(Res.string.submit),
                    onClick = {
                        onAction(ForgotPasswordAction.OnSubmitClick)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && state.canSubmit,
                    isLoading = state.isLoading
                )

                if (state.isEmailSentSuccessfully) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.forgot_password_email_sent_successfully),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.extended.success,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {},
            onBackClick = {}
        )
    }
}