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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.incognito_activate_button
import aura.feature.home.presentation.generated.resources.incognito_deactivate_button
import aura.feature.home.presentation.generated.resources.incognito_desc_off
import aura.feature.home.presentation.generated.resources.incognito_desc_on
import aura.feature.home.presentation.generated.resources.incognito_feature_1_desc
import aura.feature.home.presentation.generated.resources.incognito_feature_1_title
import aura.feature.home.presentation.generated.resources.incognito_feature_2_desc
import aura.feature.home.presentation.generated.resources.incognito_feature_2_title
import aura.feature.home.presentation.generated.resources.incognito_feature_3_desc
import aura.feature.home.presentation.generated.resources.incognito_feature_3_title
import aura.feature.home.presentation.generated.resources.incognito_subtitle_off
import aura.feature.home.presentation.generated.resources.incognito_subtitle_on
import aura.feature.home.presentation.generated.resources.incognito_title
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// TODO: verificar isPremium antes de permitir activar

@Composable
fun IncognitoModeScreen(
    onBack: () -> Unit,
    onNavigateToFeed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isIncognito = state.isIncognito

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnIncognitoToggled -> onNavigateToFeed()
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.incognito_title),
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
                    .border(
                        width = 2.dp,
                        color = if (isIncognito) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncognito) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = if (isIncognito) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isIncognito) {
                    stringResource(Res.string.incognito_subtitle_on)
                } else {
                    stringResource(Res.string.incognito_subtitle_off)
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isIncognito) {
                    stringResource(Res.string.incognito_desc_on)
                } else {
                    stringResource(Res.string.incognito_desc_off)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            IncognitoFeatureItem(
                icon = Icons.Default.VisibilityOff,
                title = stringResource(Res.string.incognito_feature_1_title),
                description = stringResource(Res.string.incognito_feature_1_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            IncognitoFeatureItem(
                icon = Icons.Default.SwipeRight,
                title = stringResource(Res.string.incognito_feature_2_title),
                description = stringResource(Res.string.incognito_feature_2_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            IncognitoFeatureItem(
                icon = Icons.Default.FlashOn,
                title = stringResource(Res.string.incognito_feature_3_title),
                description = stringResource(Res.string.incognito_feature_3_desc)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChirpButton(
                text = if (isIncognito) {
                    stringResource(Res.string.incognito_deactivate_button)
                } else {
                    stringResource(Res.string.incognito_activate_button)
                },
                onClick = { viewModel.onAction(SettingsAction.OnToggleIncognitoClick) },
                style = if (isIncognito) AppButtonStyle.SECONDARY else AppButtonStyle.PRIMARY,
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IncognitoFeatureItem(
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
