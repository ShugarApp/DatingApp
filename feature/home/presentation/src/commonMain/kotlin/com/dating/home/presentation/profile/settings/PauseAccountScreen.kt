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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import shugar.feature.home.presentation.generated.resources.do_you_want_to_pause
import shugar.feature.home.presentation.generated.resources.do_you_want_to_pause_desc
import shugar.feature.home.presentation.generated.resources.do_you_want_to_resume
import shugar.feature.home.presentation.generated.resources.do_you_want_to_resume_desc
import shugar.feature.home.presentation.generated.resources.pause
import shugar.feature.home.presentation.generated.resources.pause_account_button
import shugar.feature.home.presentation.generated.resources.pause_account_desc
import shugar.feature.home.presentation.generated.resources.pause_account_feature_1_desc
import shugar.feature.home.presentation.generated.resources.pause_account_feature_1_title
import shugar.feature.home.presentation.generated.resources.pause_account_feature_2_desc
import shugar.feature.home.presentation.generated.resources.pause_account_feature_2_title
import shugar.feature.home.presentation.generated.resources.pause_account_feature_3_desc
import shugar.feature.home.presentation.generated.resources.pause_account_feature_3_title
import shugar.feature.home.presentation.generated.resources.pause_account_subtitle
import shugar.feature.home.presentation.generated.resources.pause_account_title
import shugar.feature.home.presentation.generated.resources.resume
import shugar.feature.home.presentation.generated.resources.resume_account_button
import shugar.feature.home.presentation.generated.resources.resume_account_desc
import shugar.feature.home.presentation.generated.resources.resume_account_subtitle
import shugar.feature.home.presentation.generated.resources.resume_account_title
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PauseAccountScreen(
    onBack: () -> Unit,
    onNavigateToFeed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isAccountPaused = state.isAccountPaused
    var showConfirmDialog by remember { mutableStateOf(false) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnPauseAccountToggled -> onNavigateToFeed()
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppCenterTopBar(
                title = if (isAccountPaused) {
                    stringResource(Res.string.resume_account_title)
                } else {
                    stringResource(Res.string.pause_account_title)
                },
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
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAccountPaused) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isAccountPaused) {
                    stringResource(Res.string.resume_account_subtitle)
                } else {
                    stringResource(Res.string.pause_account_subtitle)
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isAccountPaused) {
                    stringResource(Res.string.resume_account_desc)
                } else {
                    stringResource(Res.string.pause_account_desc)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PauseFeatureItem(
                icon = Icons.Default.VisibilityOff,
                title = stringResource(Res.string.pause_account_feature_1_title),
                description = stringResource(Res.string.pause_account_feature_1_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PauseFeatureItem(
                icon = Icons.Default.SaveAlt,
                title = stringResource(Res.string.pause_account_feature_2_title),
                description = stringResource(Res.string.pause_account_feature_2_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PauseFeatureItem(
                icon = Icons.Default.Restore,
                title = stringResource(Res.string.pause_account_feature_3_title),
                description = stringResource(Res.string.pause_account_feature_3_desc)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChirpButton(
                text = if (isAccountPaused) {
                    stringResource(Res.string.resume_account_button)
                } else {
                    stringResource(Res.string.pause_account_button)
                },
                onClick = { showConfirmDialog = true },
                style = AppButtonStyle.DESTRUCTIVE_SECONDARY,
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showConfirmDialog) {
            DestructiveConfirmationDialog(
                title = if (isAccountPaused) {
                    stringResource(Res.string.do_you_want_to_resume)
                } else {
                    stringResource(Res.string.do_you_want_to_pause)
                },
                description = if (isAccountPaused) {
                    stringResource(Res.string.do_you_want_to_resume_desc)
                } else {
                    stringResource(Res.string.do_you_want_to_pause_desc)
                },
                confirmButtonText = if (isAccountPaused) {
                    stringResource(Res.string.resume)
                } else {
                    stringResource(Res.string.pause)
                },
                cancelButtonText = stringResource(Res.string.cancel),
                onDismiss = { showConfirmDialog = false },
                onCancelClick = { showConfirmDialog = false },
                onConfirmClick = {
                    showConfirmDialog = false
                    viewModel.onAction(SettingsAction.OnConfirmPauseAccountClick)
                }
            )
        }
    }
}

@Composable
private fun PauseFeatureItem(
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
