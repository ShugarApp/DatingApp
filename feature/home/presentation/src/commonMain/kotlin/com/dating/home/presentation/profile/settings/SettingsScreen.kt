package com.dating.home.presentation.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import shugar.feature.home.presentation.generated.resources.cancel
import shugar.feature.home.presentation.generated.resources.do_you_want_to_logout
import shugar.feature.home.presentation.generated.resources.do_you_want_to_logout_desc
import shugar.feature.home.presentation.generated.resources.logout
import shugar.feature.home.presentation.generated.resources.settings_100_km
import shugar.feature.home.presentation.generated.resources.settings_18_35
import shugar.feature.home.presentation.generated.resources.settings_account
import shugar.feature.home.presentation.generated.resources.settings_age_range
import shugar.feature.home.presentation.generated.resources.settings_change_password
import shugar.feature.home.presentation.generated.resources.settings_current_location
import shugar.feature.home.presentation.generated.resources.settings_discovery
import shugar.feature.home.presentation.generated.resources.settings_email_notifications
import shugar.feature.home.presentation.generated.resources.settings_guidelines
import shugar.feature.home.presentation.generated.resources.settings_help
import shugar.feature.home.presentation.generated.resources.settings_legal
import shugar.feature.home.presentation.generated.resources.settings_licenses
import shugar.feature.home.presentation.generated.resources.settings_location
import shugar.feature.home.presentation.generated.resources.settings_manage_subscription
import shugar.feature.home.presentation.generated.resources.settings_max_distance
import shugar.feature.home.presentation.generated.resources.settings_notifications
import shugar.feature.home.presentation.generated.resources.settings_privacy
import shugar.feature.home.presentation.generated.resources.settings_pro_plan_active
import shugar.feature.home.presentation.generated.resources.settings_push_notifications
import shugar.feature.home.presentation.generated.resources.settings_restore_purchases
import shugar.feature.home.presentation.generated.resources.settings_safety
import shugar.feature.home.presentation.generated.resources.settings_show_me
import shugar.feature.home.presentation.generated.resources.settings_subscription
import shugar.feature.home.presentation.generated.resources.settings_terms
import shugar.feature.home.presentation.generated.resources.settings_title
import shugar.feature.home.presentation.generated.resources.settings_version
import shugar.feature.home.presentation.generated.resources.settings_women
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnLogoutSuccess -> onLogout()
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

            // Settings List
            // Discovery Settings
            AccessCardList(
                title = stringResource(Res.string.settings_discovery)
            ) {
                AccessCardItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(Res.string.settings_location),
                    subtitle = stringResource(Res.string.settings_current_location),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Tune,
                    title = stringResource(Res.string.settings_max_distance),
                    subtitle = stringResource(Res.string.settings_100_km),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Groups,
                    title = stringResource(Res.string.settings_show_me),
                    subtitle = stringResource(Res.string.settings_women),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Cake,
                    title = stringResource(Res.string.settings_age_range),
                    subtitle = stringResource(Res.string.settings_18_35),
                    onClick = { /* TODO */ }
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
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Restore,
                    title = stringResource(Res.string.settings_restore_purchases),
                    onClick = { /* TODO */ }
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
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Mail,
                    title = stringResource(Res.string.settings_email_notifications),
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legal & Contact
            AccessCardList(
                title = stringResource(Res.string.settings_legal)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Help,
                    title = stringResource(Res.string.settings_help),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.settings_safety),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(Res.string.settings_privacy),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Description,
                    title = stringResource(Res.string.settings_terms),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Gavel,
                    title = stringResource(Res.string.settings_guidelines),
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Info,
                    title = stringResource(Res.string.settings_licenses),
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            ChirpButton(
                text = stringResource(Res.string.logout),
                onClick = { viewModel.onAction(SettingsAction.OnLogoutClick) },
                style = AppButtonStyle.DESTRUCTIVE_SECONDARY,
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
    }
}
