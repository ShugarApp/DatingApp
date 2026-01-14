package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.designsystem.components.avatar.AvatarSize
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.theme.extended
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize().padding(top = 32.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header (Avatar + Name)
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                ChirpAvatarPhoto(
                    displayText = state.userInitials,
                    size = AvatarSize.PROFILE,
                    imageUrl = state.profilePictureUrl,
                    onClick = {},
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Name & Age
            Text(
                text = "${state.username}, 26",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 3. Action Buttons Row (Dashboard Cards)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileDashboardCard(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    onClick = onEditProfile,
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Verified, // Boost/Bolt icon
                    text = "Verify",
                    onClick = onVerification,
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // 4. SAFETY & LEGAL
            AccessCardList(title = "SAFETY & LEGAL") {
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = "Safety Center",
                    onClick = { /* Todo */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Lock, // Or Gavel/Policy
                    title = "Privacy Policy",
                    onClick = { /* Todo */ }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 4. SUPPORT
            AccessCardList(title = "SUPPORT") {
                AccessCardItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    onClick = { /* Todo */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Favorite, // Heart/Hand
                    title = "Community Guidelines",
                    onClick = { /* Todo */ }
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 30.dp))
        }
    }
}

@Composable
fun ProfileDashboardCard(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .aspectRatio(1f) // Square-ish
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.extended.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
    }
}
