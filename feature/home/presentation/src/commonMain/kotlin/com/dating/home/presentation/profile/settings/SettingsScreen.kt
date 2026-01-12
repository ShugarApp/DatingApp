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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        containerColor = MaterialTheme.colorScheme.background,
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
            AccessCardList(
                title = "Account Settings"
            ) {
                AccessCardItem(
                    icon = Icons.Default.Star,
                    title = "Manage Subscription",
                    subtitle = "PRO PLAN ACTIVE",
                    subtitleColor = MaterialTheme.colorScheme.extended.textSecondary,
                    iconBgColor = MaterialTheme.colorScheme.surfaceVariant
                )
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = "Privacy & Security"
                )
                AccessCardItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    badgeCount = 3
                )
                AccessCardItem(
                    icon = Icons.Default.Palette,
                    title = "App Appearance"
                )
                AccessCardItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support"
                )
                AccessCardItem(
                    icon = Icons.Default.Info,
                    title = "About"
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
