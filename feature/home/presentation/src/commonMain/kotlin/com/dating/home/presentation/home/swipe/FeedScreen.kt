package com.dating.home.presentation.home.swipe

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.app_name_feed
import aura.feature.home.presentation.generated.resources.feed_empty_state_button
import aura.feature.home.presentation.generated.resources.feed_empty_state_title
import aura.feature.home.presentation.generated.resources.feed_filter_apply
import aura.feature.home.presentation.generated.resources.feed_filter_max_distance
import aura.feature.home.presentation.generated.resources.feed_filter_no_limit
import aura.feature.home.presentation.generated.resources.feed_filter_title
import aura.feature.home.presentation.generated.resources.feed_match_dismiss
import aura.feature.home.presentation.generated.resources.feed_match_title
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.header.MainTopAppBar
import com.dating.home.presentation.home.swipe.components.SwipeableCard
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    onAction: (FeedAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopAppBar(
                title = stringResource(Res.string.app_name_feed),
                actions = {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.feedItems.isEmpty()) {
                EmptyFeedState(onRefresh = { onAction(FeedAction.OnRefresh) })
            } else {
                val visibleItems = state.feedItems.take(3).reversed()
                visibleItems.forEach { feedItem ->
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

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterSheetState
        ) {
            FilterBottomSheet(
                currentMaxDistance = state.maxDistance,
                onApply = { distance ->
                    onAction(FeedAction.OnMaxDistanceChanged(distance))
                    showFilterSheet = false
                }
            )
        }
    }

    if (state.showMatchDialog) {
        AlertDialog(
            onDismissRequest = { onAction(FeedAction.OnDismissMatchDialog) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(Res.string.feed_match_title, state.matchedUserName ?: ""),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                TextButton(onClick = { onAction(FeedAction.OnDismissMatchDialog) }) {
                    Text(stringResource(Res.string.feed_match_dismiss))
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

@Composable
private fun FilterBottomSheet(
    currentMaxDistance: Double?,
    onApply: (Double?) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(currentMaxDistance?.toFloat() ?: 0f) }
    val hasDistance = sliderValue > 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(Res.string.feed_filter_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasDistance)
                stringResource(Res.string.feed_filter_max_distance, sliderValue.roundToInt())
            else
                stringResource(Res.string.feed_filter_no_limit),
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..500f,
            steps = 49
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onApply(if (hasDistance) sliderValue.toDouble() else null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.feed_filter_apply))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EmptyFeedState(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.feed_empty_state_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(Res.string.feed_empty_state_button))
        }
    }
}
