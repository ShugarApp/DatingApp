package com.dating.home.presentation.profile.settings.changepassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.current_password
import shugar.feature.home.presentation.generated.resources.new_password
import shugar.feature.home.presentation.generated.resources.password_change_successful
import shugar.feature.home.presentation.generated.resources.password_hint
import shugar.feature.home.presentation.generated.resources.save
import shugar.feature.home.presentation.generated.resources.settings_change_password
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.textfields.ChirpPasswordTextField
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.clearFocusOnTap
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.settings_change_password),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .clearFocusOnTap()
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ChirpPasswordTextField(
                state = state.currentPasswordTextState,
                isPasswordVisible = state.isCurrentPasswordVisible,
                onToggleVisibilityClick = {
                    viewModel.onAction(ChangePasswordAction.OnToggleCurrentPasswordVisibility)
                },
                placeholder = stringResource(Res.string.current_password),
                isError = state.newPasswordError != null,
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            ChirpPasswordTextField(
                state = state.newPasswordTextState,
                isPasswordVisible = state.isNewPasswordVisible,
                onToggleVisibilityClick = {
                    viewModel.onAction(ChangePasswordAction.OnToggleNewPasswordVisibility)
                },
                placeholder = stringResource(Res.string.new_password),
                isError = state.newPasswordError != null,
                supportingText = state.newPasswordError?.asString()
                    ?: stringResource(Res.string.password_hint)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isPasswordChangeSuccessful) {
                Text(
                    text = stringResource(Res.string.password_change_successful),
                    color = MaterialTheme.colorScheme.extended.success,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ChirpButton(
                text = stringResource(Res.string.save),
                onClick = {
                    viewModel.onAction(ChangePasswordAction.OnChangePasswordClick)
                },
                enabled = state.canChangePassword,
                isLoading = state.isChangingPassword,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            )
        }
    }
}
