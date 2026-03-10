package com.dating.home.presentation.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.profile_community_guidelines
import aura.feature.home.presentation.generated.resources.profile_edit
import aura.feature.home.presentation.generated.resources.profile_header_info_format
import aura.feature.home.presentation.generated.resources.profile_help_support
import aura.feature.home.presentation.generated.resources.profile_location_update
import aura.feature.home.presentation.generated.resources.profile_location_updating
import aura.feature.home.presentation.generated.resources.profile_privacy_policy
import aura.feature.home.presentation.generated.resources.profile_safety_center
import aura.feature.home.presentation.generated.resources.profile_safety_legal
import aura.feature.home.presentation.generated.resources.profile_settings_dashboard
import aura.feature.home.presentation.generated.resources.profile_support
import aura.feature.home.presentation.generated.resources.profile_verify
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.avatar.AvatarSize
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.permissions.Permission
import com.dating.core.presentation.permissions.PermissionState
import com.dating.core.presentation.permissions.rememberPermissionController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
    val permissionController = rememberPermissionController()
    val coroutineScope = rememberCoroutineScope()
    val previewSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize().padding(top = 32.dp)
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
                    onClick = { viewModel.onAction(ProfileAction.OnAvatarClick) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Name & Location
            Text(
                text = stringResource(Res.string.profile_header_info_format, state.username, 26),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            val location = listOfNotNull(state.city, state.country).joinToString(", ")
            if (location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val permissionState = permissionController.requestPermission(Permission.LOCATION)
                        if (permissionState == PermissionState.GRANTED) {
                            viewModel.onAction(ProfileAction.OnUpdateLocation)
                        }
                    }
                },
                enabled = !state.isUpdatingLocation,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (state.isUpdatingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.profile_location_updating))
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(Res.string.profile_location_update))
                }
            }
            val locationError = state.locationError
            if (locationError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = locationError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 3. Action Buttons Row (Dashboard Cards)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileDashboardCard(
                    icon = Icons.Default.Edit,
                    text = stringResource(Res.string.profile_edit),
                    onClick = onEditProfile,
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Verified, // Boost/Bolt icon
                    text = stringResource(Res.string.profile_verify),
                    onClick = onVerification,
                    modifier = Modifier.weight(1f)
                )
                ProfileDashboardCard(
                    icon = Icons.Default.Settings,
                    text = stringResource(Res.string.profile_settings_dashboard),
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // 4. SAFETY & LEGAL
            AccessCardList(title = stringResource(Res.string.profile_safety_legal)) {
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.profile_safety_center),
                    onClick = { /* Todo */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Lock, // Or Gavel/Policy
                    title = stringResource(Res.string.profile_privacy_policy),
                    onClick = { /* Todo */ }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 4. SUPPORT
            AccessCardList(title = stringResource(Res.string.profile_support)) {
                AccessCardItem(
                    icon = Icons.Default.Help,
                    title = stringResource(Res.string.profile_help_support),
                    onClick = { /* Todo */ }
                )
                AccessCardItem(
                    icon = Icons.Default.Favorite, // Heart/Hand
                    title = stringResource(Res.string.profile_community_guidelines),
                    onClick = { /* Todo */ }
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 30.dp))
        }
    }

    if (state.showPreview) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(ProfileAction.OnDismissPreview) },
            sheetState = previewSheetState
        ) {
            if (state.isLoadingPreview) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(500.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ProfilePreviewContent(
                    state = state,
                    onClose = { viewModel.onAction(ProfileAction.OnDismissPreview) }
                )
            }
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
                tint = MaterialTheme.colorScheme.onSurface,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfilePreviewContent(
    state: ProfileState,
    onClose: () -> Unit
) {
    val location = listOfNotNull(state.city, state.country).joinToString(", ")
    val details = listOfNotNull(
        state.height?.let { "$it cm" },
        state.zodiac,
        state.smoking,
        state.drinking
    )
    val photos = state.photos.ifEmpty { listOfNotNull(state.profilePictureUrl) }
    val pagerState = rememberPagerState(pageCount = { photos.size.coerceAtLeast(1) })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Photo pager header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (photos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = photos[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 300f
                        )
                    )
            )

            // Page indicators
            if (photos.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(photos.size) { index ->
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(if (pagerState.currentPage == index) 24.dp else 16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (pagerState.currentPage == index) Color.White
                                    else Color.White.copy(alpha = 0.5f)
                                )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = state.username,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                if (location.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        // Bio
        if (!state.bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Details chips (height, zodiac, smoking, drinking)
        if (details.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                details.forEach { detail ->
                    ChirpChip(text = detail, isSelected = false)
                }
            }
        }

        // Job / Education
        val workLine = listOfNotNull(state.jobTitle, state.company).joinToString(" @ ")
        if (workLine.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = workLine,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        if (!state.education.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.education,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Interests
        if (state.interests.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.interests.forEach { interest ->
                    ChirpChip(text = interest, isSelected = true)
                }
            }
        }
    }
}
