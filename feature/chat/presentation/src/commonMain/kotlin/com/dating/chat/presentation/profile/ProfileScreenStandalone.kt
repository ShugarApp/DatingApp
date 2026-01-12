package com.dating.chat.presentation.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.chat.presentation.generated.resources.Res
import aura.feature.chat.presentation.generated.resources.cancel
import aura.feature.chat.presentation.generated.resources.do_you_want_to_logout
import aura.feature.chat.presentation.generated.resources.do_you_want_to_logout_desc
import aura.feature.chat.presentation.generated.resources.logout
import com.dating.core.designsystem.components.avatar.AvatarSize
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            ProfileEvent.OnLogoutSuccess -> onLogout()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background, // Ensure dark background effectively
        topBar = {
            // Minimal top bar or transparent to match design
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header (Avatar + Name)
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                ChirpAvatarPhoto(
                    displayText = state.userInitials,
                    size = AvatarSize.PROFILE,
                    imageUrl = state.profilePictureUrl,
                    onClick = {},
                    // Add border if supported by component, otherwise wrap or assume theme handles it
                )
                // Optional: Verification Badge overlay if verified
                 Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.background, shape = androidx.compose.foundation.shape.CircleShape)
                        .padding(2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = state.username, // In real app, might append ", Age" if available
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Subtitle placeholder if needed (e.g. Job, Location)
            // Text(
            //     text = "Product Designer, NYC",
            //     style = MaterialTheme.typography.bodyMedium,
            //     color = MaterialTheme.colorScheme.onSurfaceVariant
            // )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileActionButton(
                    icon = Icons.Default.Edit,
                    text = "EDIT",
                    onClick = onEditProfile
                )
                ProfileActionButton(
                    icon = Icons.Default.Settings,
                    text = "SETTINGS",
                    onClick = onSettings
                )
                ProfileActionButton(
                    icon = Icons.Default.Verified,
                    text = "VERIFY",
                    onClick = onVerification
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Menu List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileMenuListItem(
                    icon = Icons.Default.CreditCard,
                    text = "Suscripciones",
                    onClick = onSubscriptions
                )
                
                // Add more items here if needed to match "Privacy", "Help" etc. from design
                ProfileMenuListItem(
                    icon = Icons.Default.Lock,
                    text = "Privacidad",
                    onClick = { /* Todo */ }
                )
                 ProfileMenuListItem(
                    icon = Icons.Default.Help,
                    text = "Ayuda y Soporte",
                    onClick = { /* Todo */ }
                )

                // Add more items here if needed to match "Privacy", "Help" etc. from design
            }
            
            Spacer(modifier = Modifier.padding(bottom = 30.dp))
        }

        if(state.showLogoutConfirmationDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.do_you_want_to_logout),
                description = stringResource(Res.string.do_you_want_to_logout_desc),
                confirmButtonText = stringResource(Res.string.logout),
                cancelButtonText = stringResource(Res.string.cancel),
                onDismiss = {
                    viewModel.onAction(ProfileAction.OnDismissLogoutConfirmationDialogClick)
                },
                onCancelClick = {
                    viewModel.onAction(ProfileAction.OnDismissLogoutConfirmationDialogClick)
                },
                onConfirmClick = {
                    viewModel.onAction(ProfileAction.OnConfirmLogoutClick)
                },
            )
        }
    }
}

@Composable
fun ProfileActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh, // Dark card color
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary, // Purple/accent
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuListItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.Transparent), // Or add a background shape if desired
        verticalAlignment = Alignment.CenterVertically
    ) {
         Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
