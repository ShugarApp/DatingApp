package com.dating.home.presentation.home.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.header.MainTopAppBar
import com.dating.home.presentation.home.swipe.components.SwipeableCard
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.app_name_feed
import shugar.feature.home.presentation.generated.resources.feed_empty_state_title
import shugar.feature.home.presentation.generated.resources.feed_empty_state_button
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    state: FeedState,
    onAction: (FeedAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopAppBar(title = stringResource(Res.string.app_name_feed))
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
                // Render top cards (reversed so first in list is on top if we use z-index correctly)
                // Actually, standard iteration: last drawn is on top.
                // So we want state.feedItems[0] to be on top? No, usually 0 is current.
                // In a Box, later children cover earlier ones.
                // So we should iterate reversed, or just take the first few and draw them in reverse order.

                val visibleItems = state.feedItems.take(3).reversed()

                visibleItems.forEach { feedItem ->
                    // Only the top card (last in this reversed list, so index 0 of original list) is swipeable
                    val isTopCard = feedItem == state.feedItems.first()

                    if (isTopCard) {
                        SwipeableCard(
                            onSwipeLeft = { onAction(FeedAction.OnPass(feedItem.id)) },
                            onSwipeRight = { onAction(FeedAction.OnLikePost(feedItem.id)) },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            FeedCardContent(
                                feedItem = feedItem,
                                onClick = { onAction(FeedAction.OnUserClick(feedItem.userId, feedItem.imageUrl)) }
                            )
                        }
                    } else {
                        // Background cards (not swipeable)
                        Card(
                            modifier = Modifier
                                .padding(16.dp),
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
        // Image Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            if (feedItem.imageUrl != null) {
                AsyncImage(
                    model = feedItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }

            // Name Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "${feedItem.userName}, 25",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = feedItem.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
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
