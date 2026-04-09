package com.dating.home.presentation.home.swipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.app_name_feed
import aura.feature.home.presentation.generated.resources.empty_feed
import aura.feature.home.presentation.generated.resources.error_connection
import aura.feature.home.presentation.generated.resources.feed_complete_profile_confirm
import aura.feature.home.presentation.generated.resources.feed_complete_profile_dismiss
import aura.feature.home.presentation.generated.resources.feed_complete_profile_later_hint
import aura.feature.home.presentation.generated.resources.feed_complete_profile_message
import aura.feature.home.presentation.generated.resources.feed_complete_profile_title
import aura.feature.home.presentation.generated.resources.feed_empty_state_button
import aura.feature.home.presentation.generated.resources.feed_empty_state_desc
import aura.feature.home.presentation.generated.resources.feed_empty_state_refresh
import aura.feature.home.presentation.generated.resources.feed_empty_state_title
import aura.feature.home.presentation.generated.resources.feed_error_desc
import aura.feature.home.presentation.generated.resources.feed_error_retry
import aura.feature.home.presentation.generated.resources.feed_error_title
import aura.feature.home.presentation.generated.resources.feed_filter_age_range
import aura.feature.home.presentation.generated.resources.feed_filter_apply
import aura.feature.home.presentation.generated.resources.feed_filter_distance_value
import aura.feature.home.presentation.generated.resources.feed_filter_gender_everyone
import aura.feature.home.presentation.generated.resources.feed_filter_gender_men
import aura.feature.home.presentation.generated.resources.feed_filter_gender_women
import aura.feature.home.presentation.generated.resources.feed_filter_max_distance
import aura.feature.home.presentation.generated.resources.feed_filter_no_limit
import aura.feature.home.presentation.generated.resources.feed_filter_reset
import aura.feature.home.presentation.generated.resources.feed_filter_show_me
import aura.feature.home.presentation.generated.resources.feed_filter_title
import aura.feature.home.presentation.generated.resources.feed_filter_verified_only
import aura.feature.home.presentation.generated.resources.feed_filter_verified_only_desc
import aura.feature.home.presentation.generated.resources.feed_incognito_banner
import aura.feature.home.presentation.generated.resources.feed_paused_activate
import aura.feature.home.presentation.generated.resources.feed_paused_desc
import aura.feature.home.presentation.generated.resources.feed_paused_title
import aura.feature.home.presentation.generated.resources.feed_searching_desc
import aura.feature.home.presentation.generated.resources.feed_searching_title
import aura.feature.home.presentation.generated.resources.feed_undo_swipe
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.header.MainTopAppBar
import com.dating.core.domain.discovery.Gender
import com.dating.home.presentation.home.swipe.components.MatchCelebrationOverlay
import com.dating.home.presentation.home.swipe.components.RadarSearchAnimation
import com.dating.home.presentation.home.swipe.components.SwipeableCard
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    onAction: (FeedAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopAppBar(
                title = stringResource(Res.string.app_name_feed),
                actions = {
                    if (state.lastDislikedItem != null) {
                        IconButton(
                            onClick = { onAction(FeedAction.OnUndoSwipe) },
                            enabled = !state.isUndoing
                        ) {
                            if (state.isUndoing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = stringResource(Res.string.feed_undo_swipe)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(Res.string.feed_filter_title)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (state.isIncognitoActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.feed_incognito_banner),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.feedItems.isNotEmpty(),
            onRefresh = { onAction(FeedAction.OnRefresh) },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.hasConnectionError && !state.isLoading) {
                    ErrorFeedState(
                        onRetry = { onAction(FeedAction.OnRefresh) }
                    )
                } else if (state.isLoading && state.feedItems.isEmpty()) {
                    RadarSearchAnimation(
                        title = stringResource(Res.string.feed_searching_title),
                        subtitle = stringResource(Res.string.feed_searching_desc),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                } else if (state.feedItems.isEmpty() && !state.isLoading) {
                    EmptyFeedState(
                        onRefresh = { onAction(FeedAction.OnRefresh) },
                        onOpenFilters = { showFilterSheet = true }
                    )
                } else if (state.feedItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val visibleItems = state.feedItems.take(3).reversed()
                            visibleItems.forEach { feedItem ->
                                key(feedItem.userId) {
                                    val isTopCard = feedItem == state.feedItems.first()
                                    if (isTopCard) {
                                        SwipeableCard(
                                            onSwipeLeft = { onAction(FeedAction.OnSwipeLeft(feedItem.userId)) },
                                            onSwipeRight = { onAction(FeedAction.OnSwipeRight(feedItem.userId)) },
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            FeedCardContent(
                                                feedItem = feedItem,
                                                onClick = { onAction(FeedAction.OnUserClick(feedItem.userId, feedItem.profilePictureUrl)) }
                                            )
                                        }
                                    } else {
                                        Card(
                                            modifier = Modifier.padding(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                                            )
                                        ) {
                                            FeedCardContent(feedItem = feedItem, onClick = {})
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            FilterBottomSheet(
                currentMaxDistance = state.maxDistance,
                currentShowMe = state.showMe,
                currentMinAge = state.minAge,
                currentMaxAge = state.maxAge,
                currentShowVerifiedOnly = state.showVerifiedOnly,
                onApply = { distance, showMe, minAge, maxAge, verifiedOnly ->
                    onAction(FeedAction.OnFiltersApplied(distance, showMe, minAge, maxAge, verifiedOnly))
                    showFilterSheet = false
                }
            )
        }
    }

    if (state.showMatchDialog) {
        MatchCelebrationOverlay(
            currentUserPhotoUrl = state.currentUserPhotoUrl,
            matchedUserName = state.matchedUserName ?: "",
            matchedUserPhotoUrl = state.matchedUserPhotoUrl,
            onSendMessage = { onAction(FeedAction.OnMatchSendMessage) },
            onKeepSwiping = { onAction(FeedAction.OnDismissMatchDialog) }
        )
    }

    if (state.showCompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { onAction(FeedAction.OnDismissCompleteProfileDialog) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(Res.string.feed_complete_profile_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(Res.string.feed_complete_profile_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.feed_complete_profile_later_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onAction(FeedAction.OnCompleteProfileClick) }) {
                    Text(stringResource(Res.string.feed_complete_profile_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(FeedAction.OnDismissCompleteProfileDialog) }) {
                    Text(stringResource(Res.string.feed_complete_profile_dismiss))
                }
            }
        )
    }
}

@Composable
fun FeedCardContent(
    feedItem: FeedItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            if (feedItem.profilePictureUrl != null) {
                AsyncImage(
                    model = feedItem.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Text(
                    text = feedItem.username,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                val location = listOfNotNull(feedItem.city, feedItem.country).joinToString(", ")
                if (location.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentMaxDistance: Double?,
    currentShowMe: Gender,
    currentMinAge: Int,
    currentMaxAge: Int,
    currentShowVerifiedOnly: Boolean,
    onApply: (Double?, Gender, Int, Int, Boolean) -> Unit
) {
    var distanceValue by remember { mutableFloatStateOf(currentMaxDistance?.toFloat() ?: 0f) }
    var showMe by remember { mutableStateOf(currentShowMe) }
    var ageRange by remember { mutableStateOf(currentMinAge.toFloat()..currentMaxAge.toFloat()) }
    var showVerifiedOnly by remember { mutableStateOf(currentShowVerifiedOnly) }
    val isNoLimit = distanceValue == 0f

    val activeFilters = listOf(
        !isNoLimit,
        showMe != Gender.EVERYONE,
        ageRange.start.roundToInt() != 18 || ageRange.endInclusive.roundToInt() != 50,
        showVerifiedOnly
    ).count { it }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.feed_filter_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (activeFilters > 0) {
                    Text(
                        text = "$activeFilters active",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            TextButton(
                onClick = {
                    distanceValue = 0f
                    showMe = Gender.EVERYONE
                    ageRange = 18f..50f
                    showVerifiedOnly = false
                }
            ) {
                Text(
                    text = stringResource(Res.string.feed_filter_reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // ── Distance ──────────────────────────────────────────────────
        FilterCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterSectionLabel(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(Res.string.feed_filter_max_distance)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isNoLimit) stringResource(Res.string.feed_filter_no_limit)
                                   else stringResource(Res.string.feed_filter_distance_value, distanceValue.roundToInt()),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Slider(
                    value = distanceValue,
                    onValueChange = { distanceValue = it },
                    valueRange = 0f..500f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.feed_filter_no_limit),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "500 km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Show Me ───────────────────────────────────────────────────
        FilterCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                FilterSectionLabel(
                    icon = Icons.Default.Groups,
                    title = stringResource(Res.string.feed_filter_show_me)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Gender.entries.forEach { gender ->
                        val isSelected = showMe == gender
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .clickable { showMe = gender }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (gender) {
                                    Gender.MEN -> stringResource(Res.string.feed_filter_gender_men)
                                    Gender.WOMEN -> stringResource(Res.string.feed_filter_gender_women)
                                    Gender.EVERYONE -> stringResource(Res.string.feed_filter_gender_everyone)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Age Range ─────────────────────────────────────────────────
        FilterCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterSectionLabel(
                        icon = Icons.Default.Cake,
                        title = stringResource(Res.string.feed_filter_age_range)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${ageRange.start.roundToInt()} – ${ageRange.endInclusive.roundToInt()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterAgeBox(
                        label = "Min",
                        value = ageRange.start.roundToInt().toString(),
                        modifier = Modifier.weight(1f)
                    )
                    FilterAgeBox(
                        label = "Max",
                        value = ageRange.endInclusive.roundToInt().toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                RangeSlider(
                    value = ageRange,
                    onValueChange = { ageRange = it },
                    valueRange = 18f..80f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "18",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "80",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Verified Only ─────────────────────────────────────────────
        FilterCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showVerifiedOnly = !showVerifiedOnly }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val iconBg by animateColorAsState(
                    if (showVerifiedOnly) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                val iconTint by animateColorAsState(
                    if (showVerifiedOnly) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = iconTint
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.feed_filter_verified_only),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.feed_filter_verified_only_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = showVerifiedOnly,
                    onCheckedChange = { showVerifiedOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Apply button ──────────────────────────────────────────────
        Button(
            onClick = {
                onApply(
                    if (!isNoLimit) distanceValue.toDouble() else null,
                    showMe,
                    ageRange.start.roundToInt(),
                    ageRange.endInclusive.roundToInt(),
                    showVerifiedOnly
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (activeFilters > 0)
                    "${stringResource(Res.string.feed_filter_apply)} ($activeFilters)"
                else
                    stringResource(Res.string.feed_filter_apply),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FilterCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun FilterSectionLabel(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FilterAgeBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyFeedState(
    onRefresh: () -> Unit,
    onOpenFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_feed),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.feed_empty_state_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.feed_empty_state_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOpenFilters,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.feed_empty_state_button))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRefresh) {
            Text(stringResource(Res.string.feed_empty_state_refresh))
        }
    }
}

@Composable
private fun ErrorFeedState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.error_connection),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.feed_error_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.feed_error_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.feed_error_retry))
        }
    }
}

@Composable
fun PausedFeedScreen(
    isResuming: Boolean,
    onActivateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopAppBar(title = stringResource(Res.string.app_name_feed))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.feed_paused_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.feed_paused_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onActivateClick,
                enabled = !isResuming,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isResuming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.feed_paused_activate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
