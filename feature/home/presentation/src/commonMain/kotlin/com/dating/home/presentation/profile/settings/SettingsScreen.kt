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
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout
import aura.feature.home.presentation.generated.resources.do_you_want_to_logout_desc
import aura.feature.home.presentation.generated.resources.logout
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
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            SettingsEvent.OnLogoutSuccess -> onLogout()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = "Settings",
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
                title = "Discovery Settings"
            ) {
                AccessCardItem(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    subtitle = "My Current Location",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Tune,
                    title = "Maximum Distance",
                    subtitle = "100 km",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Groups,
                    title = "Show Me",
                    subtitle = "Women",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Cake,
                    title = "Age Range",
                    subtitle = "18 - 35",
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Settings
            AccessCardList(
                title = "Subscription & Payment"
            ) {
                AccessCardItem(
                    icon = Icons.Default.Star,
                    title = "Manage Subscription",
                    subtitle = "PRO PLAN ACTIVE",
                    iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Restore,
                    title = "Restore Purchases",
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications
            AccessCardList(
                title = "Notifications"
            ) {
                AccessCardItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Mail,
                    title = "Email Notifications",
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legal & Contact
            AccessCardList(
                title = "Legal & Contact"
            ) {
                AccessCardItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = "Safety Center",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Gavel,
                    title = "Community Guidelines",
                    onClick = { /* TODO */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Info,
                    title = "Licenses",
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
                text = "Version 1.0 (Beta)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textDisabled,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if(state.showLogoutConfirmationDialog) {
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
