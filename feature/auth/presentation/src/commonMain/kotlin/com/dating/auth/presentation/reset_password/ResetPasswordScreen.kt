package com.dating.auth.presentation.reset_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.password
import aura.feature.auth.presentation.generated.resources.password_hint
import aura.feature.auth.presentation.generated.resources.reset_password_successfully
import aura.feature.auth.presentation.generated.resources.set_new_password
import aura.feature.auth.presentation.generated.resources.submit
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.components.textfields.ChirpPasswordTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    AuthSnackbarScaffold {
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
                modifier = Modifier.size(96.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = stringResource(Res.string.set_new_password),
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
            ChirpPasswordTextField(
                state = state.passwordTextState,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = stringResource(Res.string.password_hint),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ResetPasswordAction.OnTogglePasswordVisibilityClick)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            ChirpButton(
                text = stringResource(Res.string.submit),
                onClick = {
                    onAction(ResetPasswordAction.OnSubmitClick)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.canSubmit,
                isLoading = state.isLoading
            )
            
            if(state.isResetSuccessful) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.reset_password_successfully),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}