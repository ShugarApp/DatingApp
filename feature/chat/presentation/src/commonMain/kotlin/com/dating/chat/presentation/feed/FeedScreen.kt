package com.dating.chat.presentation.feed

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.dating.chat.presentation.feed.components.SwipeableCard

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
            TopAppBar(
                title = {
                    Text(
                        text = "Descubre",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                                onClick = { onAction(FeedAction.OnUserClick(feedItem.userId)) }
                            )
                        }
                    } else {
                        // Background cards (not swipeable)
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .offset(y = 10.dp), // Slight offset for stack effect
                             colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                     text = "${feedItem.userName}, 25", // Hardcoded age for now as it's not in model
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
        
        // Action Buttons Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Pass action logic handled by parent swipe */ },
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pass",
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            IconButton(
                onClick = { /* Like action logic handled by parent swipe */ },
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = Color.Green,
                    modifier = Modifier.size(32.dp)
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
            text = "No hay más perfiles",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(onClick = onRefresh) {
            Text("Volver a cargar")
        }
    }
}
