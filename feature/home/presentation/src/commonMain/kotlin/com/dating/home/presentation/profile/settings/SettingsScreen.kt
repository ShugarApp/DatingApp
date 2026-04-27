package com.dating.home.presentation.profile.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout_desc
import aura.feature.home.presentation.generated.resources.emergency_safety_section
import aura.feature.home.presentation.generated.resources.logout
import aura.feature.home.presentation.generated.resources.network_error
import aura.feature.home.presentation.generated.resources.settings_account
import aura.feature.home.presentation.generated.resources.settings_account_privacy
import aura.feature.home.presentation.generated.resources.settings_discovery
import aura.feature.home.presentation.generated.resources.settings_filters
import aura.feature.home.presentation.generated.resources.settings_location_permission_denied
import aura.feature.home.presentation.generated.resources.settings_location_update_failed
import aura.feature.home.presentation.generated.resources.settings_location_updated
import aura.feature.home.presentation.generated.resources.settings_update_location
import aura.feature.home.presentation.generated.resources.settings_incognito_mode
import aura.feature.home.presentation.generated.resources.settings_legal_account
import aura.feature.home.presentation.generated.resources.settings_legal_section
import aura.feature.home.presentation.generated.resources.settings_pause_account
import aura.feature.home.presentation.generated.resources.settings_security
import aura.feature.home.presentation.generated.resources.settings_preferences
import aura.feature.home.presentation.generated.resources.settings_push_notifications
import aura.feature.home.presentation.generated.resources.settings_resume_account
import aura.feature.home.presentation.generated.resources.settings_safety
import aura.feature.home.presentation.generated.resources.settings_theme
import aura.feature.home.presentation.generated.resources.settings_theme_dark
import aura.feature.home.presentation.generated.resources.settings_theme_light
import aura.feature.home.presentation.generated.resources.settings_theme_system
import aura.feature.home.presentation.generated.resources.settings_title
import aura.feature.home.presentation.generated.resources.settings_version
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.preferences.ThemePreference
import com.dating.core.presentation.permissions.Permission
import com.dating.core.presentation.permissions.PermissionState
import com.dating.core.presentation.permissions.rememberPermissionController
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.core.presentation.util.rememberOpenNotificationSettings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onPauseAccount: () -> Unit,
    onIncognitoMode: () -> Unit,
    onSafetyCenter: () -> Unit = {},
    onDiscoverySettings: () -> Unit = {},
    onLegalAccount: () -> Unit = {},
    onSecuritySettings: () -> Unit = {},
    onPrivacySettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openNotificationSettings = rememberOpenNotificationSettings()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val permissionController = rememberPermissionController()
    val locationUpdatedMessage = stringResource(Res.string.settings_location_updated)
    val locationUpdateFailedMessage = stringResource(Res.string.settings_location_update_failed)
    val locationPermissionDeniedMessage = stringResource(Res.string.settings_location_permission_denied)

    val updateLocationWithPermission: () -> Unit = {
        scope.launch {
            val permState = permissionController.requestPermission(Permission.LOCATION)
            if (permState == PermissionState.GRANTED) {
                viewModel.onAction(SettingsAction.OnLocationClick)
            } else {
                snackbarHostState.showSnackbar(locationPermissionDeniedMessage)
            }
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnLogoutSuccess -> onLogout()
            SettingsEvent.OnLocationUpdated -> {
                scope.launch { snackbarHostState.showSnackbar(locationUpdatedMessage) }
            }
            SettingsEvent.OnLocationUpdateFailed -> {
                scope.launch { snackbarHostState.showSnackbar(locationUpdateFailedMessage) }
            }
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.settings_title),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Discovery Settings section
            AccessCardList(
                title = stringResource(Res.string.settings_discovery)
            ) {
                AccessCardItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(Res.string.settings_update_location),
                    subtitle = if (state.isUpdatingLocation) "Updating..." else null,
                    onClick = updateLocationWithPermission
                )
                AccessCardItem(
                    icon = Icons.Default.Tune,
                    title = stringResource(Res.string.settings_filters),
                    onClick = onDiscoverySettings
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety section
            AccessCardList(
                title = stringResource(Res.string.emergency_safety_section)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_safety),
                    onClick = onSafetyCenter
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preferences: Push Notifications + Theme
            AccessCardList(
                title = stringResource(Res.string.settings_preferences)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(Res.string.settings_push_notifications),
                    onClick = openNotificationSettings
                )
                AccessCardItem(
                    icon = Icons.Default.Palette,
                    title = stringResource(Res.string.settings_theme),
                    subtitle = when (state.themePreference) {
                        ThemePreference.LIGHT -> stringResource(Res.string.settings_theme_light)
                        ThemePreference.DARK -> stringResource(Res.string.settings_theme_dark)
                        ThemePreference.SYSTEM -> stringResource(Res.string.settings_theme_system)
                    },
                    onClick = { viewModel.onAction(SettingsAction.OnThemeClick) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Settings
            AccessCardList(
                title = stringResource(Res.string.settings_account)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_security),
                    onClick = onSecuritySettings
                )
                AccessCardItem(
                    icon = if (state.isAccountPaused) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                    title = if (state.isAccountPaused) {
                        stringResource(Res.string.settings_resume_account)
                    } else {
                        stringResource(Res.string.settings_pause_account)
                    },
                    onClick = onPauseAccount
                )
                AccessCardItem(
                    icon = Icons.Default.VisibilityOff,
                    title = stringResource(Res.string.settings_incognito_mode),
                    onClick = onIncognitoMode
                )
                AccessCardItem(
                    icon = Icons.Default.Lock,
                    title = stringResource(Res.string.settings_account_privacy),
                    onClick = onPrivacySettings
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legal section
            AccessCardList(
                title = stringResource(Res.string.settings_legal_section)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Gavel,
                    title = stringResource(Res.string.settings_legal_account),
                    onClick = onLegalAccount
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            ChirpButton(
                text = stringResource(Res.string.logout),
                onClick = { viewModel.onAction(SettingsAction.OnLogoutClick) },
                style = AppButtonStyle.DESTRUCTIVE_SECONDARY,
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.settings_version),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textDisabled,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Logout confirmation dialog
        if (state.showLogoutConfirmationDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.do_you_want_to_logout),
                description = stringResource(Res.string.do_you_want_to_logout_desc),
                confirmButtonText = stringResource(Res.string.logout),
                cancelButtonText = stringResource(Res.string.cancel),
                onDismiss = {
                    viewModel.onAction(SettingsAction.OnDismissLogoutConfirmationDialogClick)
                },
                onCancelClick = {
                    viewModel.onAction(SettingsAction.OnDismissLogoutConfirmationDialogClick)
                },
                onConfirmClick = {
                    viewModel.onAction(SettingsAction.OnConfirmLogoutClick)
                },
            )
        }

        // Error dialog
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

        // Theme dialog
        if (state.showThemeDialog) {
            ThemePickerDialog(
                currentTheme = state.themePreference,
                onDismiss = { viewModel.onAction(SettingsAction.OnThemeChanged(state.themePreference)) },
                onConfirm = { theme ->
                    viewModel.onAction(SettingsAction.OnThemeChanged(theme))
                }
            )
        }
    }
}

@Composable
private fun ThemePickerDialog(
    currentTheme: ThemePreference,
    onDismiss: () -> Unit,
    onConfirm: (ThemePreference) -> Unit
) {
    var selected by remember { mutableStateOf(currentTheme) }

    DiscoveryDialog(
        title = stringResource(Res.string.settings_theme),
        icon = Icons.Default.Palette,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemePreference.entries.forEach { theme ->
                val isSelected = selected == theme
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { selected = theme }
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (theme) {
                            ThemePreference.LIGHT -> Icons.Default.LightMode
                            ThemePreference.DARK -> Icons.Default.DarkMode
                            ThemePreference.SYSTEM -> Icons.Default.PhoneAndroid
                        },
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.extended.textSecondary
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = when (theme) {
                            ThemePreference.LIGHT -> stringResource(Res.string.settings_theme_light)
                            ThemePreference.DARK -> stringResource(Res.string.settings_theme_dark)
                            ThemePreference.SYSTEM -> stringResource(Res.string.settings_theme_system)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.extended.textSecondary
                        }
                    )
                }
            }
        }
    }
}
