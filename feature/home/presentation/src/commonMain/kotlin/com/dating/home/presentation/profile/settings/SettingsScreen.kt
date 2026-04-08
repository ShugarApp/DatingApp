package com.dating.home.presentation.profile.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout_desc
import aura.feature.home.presentation.generated.resources.logout
import aura.feature.home.presentation.generated.resources.network_error
import aura.feature.home.presentation.generated.resources.save
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.Switch
import aura.feature.home.presentation.generated.resources.emergency_contacts_title
import aura.feature.home.presentation.generated.resources.emergency_feature_description
import aura.feature.home.presentation.generated.resources.emergency_how_it_works
import aura.feature.home.presentation.generated.resources.emergency_safety_section
import aura.feature.home.presentation.generated.resources.date_safety_tips_subtitle
import aura.feature.home.presentation.generated.resources.settings_100_km
import aura.feature.home.presentation.generated.resources.settings_date_safety_tips
import aura.feature.home.presentation.generated.resources.settings_account
import aura.feature.home.presentation.generated.resources.settings_age_range
import aura.feature.home.presentation.generated.resources.settings_change_password
import aura.feature.home.presentation.generated.resources.settings_current_location
import aura.feature.home.presentation.generated.resources.settings_delete_account
import aura.feature.home.presentation.generated.resources.settings_discovery
import aura.feature.home.presentation.generated.resources.settings_appearance
import aura.feature.home.presentation.generated.resources.settings_guidelines
import aura.feature.home.presentation.generated.resources.settings_help
import aura.feature.home.presentation.generated.resources.settings_legal
import aura.feature.home.presentation.generated.resources.settings_licenses
import aura.feature.home.presentation.generated.resources.settings_location
import aura.feature.home.presentation.generated.resources.settings_manage_subscription
import aura.feature.home.presentation.generated.resources.settings_max_distance
import aura.feature.home.presentation.generated.resources.settings_notifications
import aura.feature.home.presentation.generated.resources.settings_blocked_users
import aura.feature.home.presentation.generated.resources.settings_incognito_mode
import aura.feature.home.presentation.generated.resources.settings_pause_account
import aura.feature.home.presentation.generated.resources.settings_privacy
import aura.feature.home.presentation.generated.resources.settings_pro_plan_active
import aura.feature.home.presentation.generated.resources.settings_push_notifications
import aura.feature.home.presentation.generated.resources.settings_restore_purchases
import aura.feature.home.presentation.generated.resources.settings_resume_account
import aura.feature.home.presentation.generated.resources.settings_safety
import aura.feature.home.presentation.generated.resources.settings_show_me
import aura.feature.home.presentation.generated.resources.settings_subscription
import aura.feature.home.presentation.generated.resources.settings_terms
import aura.feature.home.presentation.generated.resources.settings_title
import aura.feature.home.presentation.generated.resources.settings_theme
import aura.feature.home.presentation.generated.resources.settings_theme_dark
import aura.feature.home.presentation.generated.resources.settings_theme_light
import aura.feature.home.presentation.generated.resources.settings_theme_system
import aura.feature.home.presentation.generated.resources.settings_version
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.discovery.Gender
import com.dating.core.domain.preferences.ThemePreference
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.core.presentation.util.rememberOpenNotificationSettings
import com.dating.core.presentation.util.rememberOpenUrl
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

private const val URL_HELP = "https://aura-safe-dating.com/#"
private const val URL_SAFETY = "https://aura-safe-dating.com/#"
private const val URL_PRIVACY = "https://aura-safe-dating.com/#"
private const val URL_TERMS = "https://aura-safe-dating.com/#"
private const val URL_GUIDELINES = "https://aura-safe-dating.com/#"
private const val URL_LICENSES = "https://aura-safe-dating.com/#"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onPauseAccount: () -> Unit,
    onIncognitoMode: () -> Unit,
    onBlockedUsers: () -> Unit,
    onEmergencyContacts: () -> Unit = {},
    onEmergencyTutorial: () -> Unit = {},
    onDateSafetyTips: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openUrl = rememberOpenUrl()
    val openNotificationSettings = rememberOpenNotificationSettings()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnLogoutSuccess -> onLogout()
            SettingsEvent.OnNavigateToEmergencyContacts -> onEmergencyContacts()
            SettingsEvent.OnNavigateToEmergencyTutorial -> onEmergencyTutorial()
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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

            // Discovery Settings
            AccessCardList(
                title = stringResource(Res.string.settings_discovery)
            ) {
                AccessCardItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(Res.string.settings_location),
                    subtitle = if (state.isUpdatingLocation) {
                        "Updating..."
                    } else {
                        stringResource(Res.string.settings_current_location)
                    },
                    onClick = { viewModel.onAction(SettingsAction.OnLocationClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Tune,
                    title = stringResource(Res.string.settings_max_distance),
                    subtitle = state.maxDistance?.let { "${it.roundToInt()} km" }
                        ?: stringResource(Res.string.settings_100_km),
                    onClick = { viewModel.onAction(SettingsAction.OnMaxDistanceClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Groups,
                    title = stringResource(Res.string.settings_show_me),
                    subtitle = when (state.showMe) {
                        Gender.MEN -> "Men"
                        Gender.WOMEN -> "Women"
                        Gender.EVERYONE -> "Everyone"
                    },
                    onClick = { viewModel.onAction(SettingsAction.OnShowMeClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Cake,
                    title = stringResource(Res.string.settings_age_range),
                    subtitle = "${state.minAge} - ${state.maxAge}",
                    onClick = { viewModel.onAction(SettingsAction.OnAgeRangeClick) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Settings
            AccessCardList(
                title = stringResource(Res.string.settings_account)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_change_password),
                    onClick = onChangePassword
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
                    icon = Icons.Default.Block,
                    title = stringResource(Res.string.settings_blocked_users),
                    onClick = onBlockedUsers
                )
                AccessCardItem(
                    icon = Icons.Default.PersonOff,
                    title = stringResource(Res.string.settings_delete_account),
                    onClick = onDeleteAccount
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety / Emergency Contacts
            AccessCardList(
                title = stringResource(Res.string.emergency_safety_section)
            ) {
                // Toggle row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactPhone,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.emergency_contacts_title),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.extended.textPrimary
                        )
                        Text(
                            text = stringResource(Res.string.emergency_feature_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isEmergencyEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onAction(SettingsAction.OnEmergencyToggle(enabled))
                        }
                    )
                }

                if (state.isEmergencyEnabled) {
                    AccessCardItem(
                        icon = Icons.Default.ContactPhone,
                        title = stringResource(Res.string.emergency_contacts_title),
                        subtitle = stringResource(Res.string.emergency_feature_description),
                        onClick = { viewModel.onAction(SettingsAction.OnEmergencyContactsClick) }
                    )
                }
                AccessCardItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = stringResource(Res.string.emergency_how_it_works),
                    onClick = { viewModel.onAction(SettingsAction.OnEmergencyTutorialClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_date_safety_tips),
                    subtitle = stringResource(Res.string.date_safety_tips_subtitle),
                    onClick = onDateSafetyTips
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Settings
            AccessCardList(
                title = stringResource(Res.string.settings_subscription)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Star,
                    title = stringResource(Res.string.settings_manage_subscription),
                    subtitle = stringResource(Res.string.settings_pro_plan_active),
                    iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { /* Requires platform store integration */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Restore,
                    title = stringResource(Res.string.settings_restore_purchases),
                    onClick = { /* Requires platform store integration */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications
            AccessCardList(
                title = stringResource(Res.string.settings_notifications)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(Res.string.settings_push_notifications),
                    onClick = openNotificationSettings
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appearance
            AccessCardList(
                title = stringResource(Res.string.settings_appearance)
            ) {
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

            // Legal & Contact
            AccessCardList(
                title = stringResource(Res.string.settings_legal)
            ) {
                AccessCardItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = stringResource(Res.string.settings_help),
                    onClick = { openUrl(URL_HELP) }
                )
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_safety),
                    onClick = { openUrl(URL_SAFETY) }
                )
                AccessCardItem(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(Res.string.settings_privacy),
                    onClick = { openUrl(URL_PRIVACY) }
                )
                AccessCardItem(
                    icon = Icons.Default.Description,
                    title = stringResource(Res.string.settings_terms),
                    onClick = { openUrl(URL_TERMS) }
                )
                AccessCardItem(
                    icon = Icons.Default.Gavel,
                    title = stringResource(Res.string.settings_guidelines),
                    onClick = { openUrl(URL_GUIDELINES) }
                )
                AccessCardItem(
                    icon = Icons.Default.Info,
                    title = stringResource(Res.string.settings_licenses),
                    onClick = { openUrl(URL_LICENSES) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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

        // Max Distance dialog
        if (state.showDistanceDialog) {
            MaxDistanceDialog(
                currentDistance = state.maxDistance ?: 100.0,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { distance ->
                    viewModel.onAction(SettingsAction.OnMaxDistanceChanged(distance))
                }
            )
        }

        // Show Me dialog
        if (state.showGenderDialog) {
            ShowMeDialog(
                currentGender = state.showMe,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { gender ->
                    viewModel.onAction(SettingsAction.OnShowMeChanged(gender))
                }
            )
        }

        // Age Range dialog
        if (state.showAgeRangeDialog) {
            AgeRangeDialog(
                currentMin = state.minAge,
                currentMax = state.maxAge,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { min, max ->
                    viewModel.onAction(SettingsAction.OnAgeRangeChanged(min, max))
                }
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
            ThemeDialog(
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
private fun MaxDistanceDialog(
    currentDistance: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit
) {
    var distance by remember { mutableFloatStateOf(currentDistance.toFloat()) }

    SettingsDialog(
        title = stringResource(Res.string.settings_max_distance),
        icon = Icons.Default.Tune,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(if (distance >= 500f) null else distance.toDouble())
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (distance >= 500f) "No limit" else "${distance.roundToInt()} km",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = distance,
            onValueChange = { distance = it },
            valueRange = 5f..500f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "5 km",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
            Text(
                text = "No limit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}

@Composable
private fun ShowMeDialog(
    currentGender: Gender,
    onDismiss: () -> Unit,
    onConfirm: (Gender) -> Unit
) {
    var selected by remember { mutableStateOf(currentGender) }

    SettingsDialog(
        title = stringResource(Res.string.settings_show_me),
        icon = Icons.Default.Groups,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Gender.entries.forEach { gender ->
                val isSelected = selected == gender
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { selected = gender }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = when (gender) {
                            Gender.MEN -> "Men"
                            Gender.WOMEN -> "Women"
                            Gender.EVERYONE -> "Everyone"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.extended.textPrimary
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgeRangeDialog(
    currentMin: Int,
    currentMax: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var range by remember {
        mutableStateOf(currentMin.toFloat()..currentMax.toFloat())
    }

    SettingsDialog(
        title = stringResource(Res.string.settings_age_range),
        icon = Icons.Default.Cake,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(range.start.roundToInt(), range.endInclusive.roundToInt())
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            AgeValueBox(
                label = "Min",
                value = "${range.start.roundToInt()}"
            )
            AgeValueBox(
                label = "Max",
                value = "${range.endInclusive.roundToInt()}"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        RangeSlider(
            value = range,
            onValueChange = { range = it },
            valueRange = 18f..80f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "18",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
            Text(
                text = "80",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}

@Composable
private fun AgeValueBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ThemeDialog(
    currentTheme: ThemePreference,
    onDismiss: () -> Unit,
    onConfirm: (ThemePreference) -> Unit
) {
    var selected by remember { mutableStateOf(currentTheme) }

    SettingsDialog(
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

@Composable
private fun SettingsDialog(
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .widthIn(max = 480.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.extended.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChirpButton(
                        text = stringResource(Res.string.cancel),
                        onClick = onDismiss,
                        style = AppButtonStyle.SECONDARY
                    )
                    ChirpButton(
                        text = stringResource(Res.string.save),
                        onClick = onConfirm,
                        style = AppButtonStyle.PRIMARY
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}
