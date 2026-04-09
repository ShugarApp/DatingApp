package com.dating.home.presentation.profile.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.date_safety_checklist_section
import aura.feature.home.presentation.generated.resources.date_safety_tips_section
import aura.feature.home.presentation.generated.resources.profile_completion_cta
import aura.feature.home.presentation.generated.resources.profile_completion_hint
import aura.feature.home.presentation.generated.resources.profile_completion_percent
import aura.feature.home.presentation.generated.resources.profile_completion_title
import aura.feature.home.presentation.generated.resources.profile_edit
import aura.feature.home.presentation.generated.resources.profile_header_info_format
import aura.feature.home.presentation.generated.resources.profile_safety_center
import aura.feature.home.presentation.generated.resources.profile_safety_section
import aura.feature.home.presentation.generated.resources.profile_settings_dashboard
import aura.feature.home.presentation.generated.resources.profile_sos_button
import aura.feature.home.presentation.generated.resources.safe_date_title
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.avatar.AvatarSize
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.auth.VerificationStatus
import com.dating.home.presentation.components.VerifiedBadge
import com.dating.home.presentation.components.VerifiedBlue
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    onSafetyCenter: () -> Unit,
    onDateSafetyTips: () -> Unit,
    onDateSafetyChecklist: () -> Unit,
    onSafeDate: () -> Unit,
    showSosButton: Boolean,
    onSosTrigger: () -> Unit,
    onNavigateToProfile: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                    onClick = { onNavigateToProfile(state.userId, state.profilePictureUrl) },
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
            Spacer(modifier = Modifier.height(24.dp))

            // 3. Verification status banner — shown above action buttons
            VerificationStatusCard(
                status = state.verificationStatus,
                onVerifyClick = onVerification,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (state.profileCompletion < 100) {
                Spacer(modifier = Modifier.height(12.dp))
                ProfileCompletionCard(
                    completion = state.profileCompletion,
                    onCompleteClick = onEditProfile,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Action Buttons Row — Edit | Safety Center | Settings
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
                    icon = Icons.Default.Security,
                    text = stringResource(Res.string.profile_safety_center),
                    onClick = onSafetyCenter,
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

            // 5. SOS Button — shown above Safety Tips when emergency feature is enabled
            if (showSosButton) {
                SosActionButton(
                    onClick = onSosTrigger,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 6. SAFETY
            AccessCardList(title = stringResource(Res.string.profile_safety_section)) {
                AccessCardItem(
                    icon = Icons.Default.Info,
                    title = stringResource(Res.string.date_safety_tips_section),
                    onClick = onDateSafetyTips
                )
                AccessCardItem(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(Res.string.date_safety_checklist_section),
                    onClick = onDateSafetyChecklist
                )
                AccessCardItem(
                    icon = Icons.Default.DateRange,
                    title = stringResource(Res.string.safe_date_title),
                    onClick = onSafeDate
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
fun ProfileCompletionCard(
    completion: Int,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(completion) {
        animatedProgress = completion / 100f
    }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "profile_completion_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.profile_completion_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Text(
                    text = stringResource(Res.string.profile_completion_percent, completion),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.profile_completion_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(onClick = onCompleteClick) {
                    Text(
                        text = stringResource(Res.string.profile_completion_cta),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationStatusCard(
    status: VerificationStatus,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, text, tint, bgTint) = when (status) {
        VerificationStatus.VERIFIED -> Quadruple(
            Icons.Default.CheckCircle,
            "Profile Verified",
            Color(0xFF4CAF50),
            Color(0xFF4CAF50).copy(alpha = 0.12f)
        )
        VerificationStatus.PENDING -> Quadruple(
            Icons.Default.HourglassEmpty,
            "Verification pending...",
            Color(0xFFFFA000),
            Color(0xFFFFA000).copy(alpha = 0.12f)
        )
        VerificationStatus.REJECTED -> Quadruple(
            Icons.Default.Error,
            "Verification rejected — tap to retry",
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        )
        VerificationStatus.UNVERIFIED -> Quadruple(
            Icons.Default.Verified,
            "Verify your profile",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    }

    val isClickable = status == VerificationStatus.UNVERIFIED || status == VerificationStatus.REJECTED

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgTint)
            .then(
                if (isClickable) Modifier.clickable(onClick = onVerifyClick)
                else Modifier
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == VerificationStatus.VERIFIED) {
            VerifiedBadge(size = 22.dp, tint = VerifiedBlue)
        } else {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = tint,
            modifier = Modifier.weight(1f)
        )
        if (isClickable) {
            Text(
                text = "→",
                style = MaterialTheme.typography.bodyLarge,
                color = tint
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun ProfileDashboardCard(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.extended.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

private val SosRed = Color(0xFFE53935)
private val SosPulseRing = Color(0xFFEF9A9A)

@Composable
internal fun SosActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Animated SOS circle — outer container sized so the scaled ring never clips
        // ring = 48dp, scale max = 1.4 → 67dp → fits inside 72dp container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(72.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(SosPulseRing.copy(alpha = 0.5f))
            )
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SosRed),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SOS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.profile_sos_button),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "Tap to trigger emergency countdown",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            )
        }
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
