package com.dating.home.presentation.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.cancel
import shugar.feature.home.presentation.generated.resources.delete_account
import shugar.feature.home.presentation.generated.resources.delete_account_button
import shugar.feature.home.presentation.generated.resources.delete_account_desc
import shugar.feature.home.presentation.generated.resources.delete_account_feature_1_desc
import shugar.feature.home.presentation.generated.resources.delete_account_feature_1_title
import shugar.feature.home.presentation.generated.resources.delete_account_feature_2_desc
import shugar.feature.home.presentation.generated.resources.delete_account_feature_2_title
import shugar.feature.home.presentation.generated.resources.delete_account_feature_3_desc
import shugar.feature.home.presentation.generated.resources.delete_account_feature_3_title
import shugar.feature.home.presentation.generated.resources.delete_account_subtitle
import shugar.feature.home.presentation.generated.resources.delete_account_title
import shugar.feature.home.presentation.generated.resources.do_you_want_to_delete_account
import shugar.feature.home.presentation.generated.resources.do_you_want_to_delete_account_desc
import shugar.feature.home.presentation.generated.resources.network_error
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeleteAccountScreen(
    onBack: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnDeleteAccountSuccess -> onDeleteAccount()
            SettingsEvent.OnSurveyCompleted -> showConfirmDialog = true
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.delete_account_title),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(32.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.delete_account_subtitle),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.delete_account_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            DeleteFeatureItem(
                icon = Icons.Default.DeleteForever,
                title = stringResource(Res.string.delete_account_feature_1_title),
                description = stringResource(Res.string.delete_account_feature_1_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DeleteFeatureItem(
                icon = Icons.Default.Warning,
                title = stringResource(Res.string.delete_account_feature_2_title),
                description = stringResource(Res.string.delete_account_feature_2_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DeleteFeatureItem(
                icon = Icons.Default.Block,
                title = stringResource(Res.string.delete_account_feature_3_title),
                description = stringResource(Res.string.delete_account_feature_3_desc)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChirpButton(
                text = stringResource(Res.string.delete_account_button),
                onClick = { viewModel.onAction(SettingsAction.OnDeleteAccountClick) },
                style = AppButtonStyle.DESTRUCTIVE_PRIMARY,
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.showDeleteSurveyDialog) {
            DeleteSurveyDialog(
                selectedReason = state.selectedDeleteReason,
                onReasonSelected = { viewModel.onAction(SettingsAction.OnSurveyReasonSelected(it)) },
                onContinue = { viewModel.onAction(SettingsAction.OnSurveyConfirmClick) },
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissSurvey) }
            )
        }

        if (showConfirmDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.do_you_want_to_delete_account),
                description = stringResource(Res.string.do_you_want_to_delete_account_desc),
                confirmButtonText = stringResource(Res.string.delete_account),
                cancelButtonText = stringResource(Res.string.cancel),
                onDismiss = { showConfirmDialog = false },
                onCancelClick = { showConfirmDialog = false },
                onConfirmClick = {
                    showConfirmDialog = false
                    viewModel.onAction(SettingsAction.OnConfirmDeleteAccountClick)
                }
            )
        }

        state.errorMessage?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = { viewModel.onAction(SettingsAction.OnDismissError) },
                title = { Text(stringResource(Res.string.network_error)) },
                text = { Text(errorMessage.asString()) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(SettingsAction.OnDismissError) }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun DeleteFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}
