package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dating.home.domain.models.MessageType
import com.dating.home.presentation.chat.audioplayer.rememberAudioPlayer

@Composable
fun MediaMessageContent(
    content: String,
    messageType: MessageType,
    modifier: Modifier = Modifier,
    onImageClick: (() -> Unit)? = null
) {
    when (messageType) {
        MessageType.IMAGE -> {
            ImageMessageContent(
                imageUrl = content,
                onClick = onImageClick,
                modifier = modifier
            )
        }
        MessageType.GIF -> {
            GifMessageContent(
                gifUrl = content,
                modifier = modifier
            )
        }
        MessageType.AUDIO -> {
            AudioMessageContent(
                audioUrl = content,
                modifier = modifier
            )
        }
        MessageType.TEXT -> Unit
        MessageType.DATE_PROPOSAL -> Unit // Handled separately via DateProposalBubbleContent
        MessageType.LOCATION -> Unit // Handled separately via LocationBubbleContent
    }
}

@Composable
private fun ImageMessageContent(
    imageUrl: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Photo message",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Composable
private fun GifMessageContent(
    gifUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = gifUrl,
            contentDescription = "GIF message",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Composable
private fun AudioMessageContent(
    audioUrl: String,
    modifier: Modifier = Modifier
) {
    val player = rememberAudioPlayer()

    val progress by animateFloatAsState(
        targetValue = if (player.durationMs > 0) {
            player.currentPositionMs.toFloat() / player.durationMs.toFloat()
        } else 0f,
        label = "audio_progress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play(audioUrl)
                }
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = if (player.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (player.isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )

        Text(
            text = formatTime(
                if (player.isPlaying || player.currentPositionMs > 0) player.currentPositionMs
                else player.durationMs
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"
    return "$minutes:$secondsStr"
}
