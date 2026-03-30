package com.dating.home.presentation.chat.audiorecorder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dating.home.presentation.profile.mediapicker.PickedImageData
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class RecorderState {
    IDLE, RECORDING, PAUSED, STOPPED
}

@Composable
fun AudioRecorderSheet(
    onDismiss: () -> Unit,
    onRecordingComplete: (PickedImageData) -> Unit
) {
    val recorder = rememberPlatformAudioRecorder()
    var state by remember { mutableStateOf(RecorderState.IDLE) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var recordedBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(state) {
        if (state == RecorderState.RECORDING) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recorder.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            RecorderState.IDLE -> IdleContent(
                onStartRecording = {
                    recorder.startRecording()
                    state = RecorderState.RECORDING
                }
            )
            RecorderState.RECORDING -> RecordingContent(
                elapsedSeconds = elapsedSeconds,
                onPause = {
                    recorder.pauseRecording()
                    state = RecorderState.PAUSED
                },
                onStop = {
                    recordedBytes = recorder.stopRecording()
                    state = RecorderState.STOPPED
                },
                onCancel = {
                    recorder.release()
                    onDismiss()
                }
            )
            RecorderState.PAUSED -> PausedContent(
                elapsedSeconds = elapsedSeconds,
                onResume = {
                    recorder.resumeRecording()
                    state = RecorderState.RECORDING
                },
                onStop = {
                    recordedBytes = recorder.stopRecording()
                    state = RecorderState.STOPPED
                },
                onCancel = {
                    recorder.release()
                    onDismiss()
                }
            )
            RecorderState.STOPPED -> StoppedContent(
                elapsedSeconds = elapsedSeconds,
                onSend = {
                    val bytes = recordedBytes
                    if (bytes != null && bytes.isNotEmpty()) {
                        onRecordingComplete(
                            PickedImageData(
                                bytes = bytes,
                                mimeType = "audio/mp4"
                            )
                        )
                    }
                },
                onDelete = {
                    recorder.release()
                    recordedBytes = null
                    onDismiss()
                },
                onReRecord = {
                    recordedBytes = null
                    elapsedSeconds = 0
                    recorder.startRecording()
                    state = RecorderState.RECORDING
                }
            )
        }
    }
}

@Composable
private fun IdleContent(onStartRecording: () -> Unit) {
    Spacer(modifier = Modifier.height(24.dp))

    IconButton(
        onClick = onStartRecording,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color(0xFFEF5350))
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Start recording",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Tap to record",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun RecordingContent(
    elapsedSeconds: Int,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit
) {
    // Pulsing red dot + timer
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        PulsingRedDot()
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = formatDuration(elapsedSeconds),
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFEF5350)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Waveform
    WaveformBars(isAnimating = true)

    Spacer(modifier = Modifier.height(24.dp))

    // Buttons: Delete | Pause | Stop
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Delete
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Cancel",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        // Pause
        IconButton(
            onClick = onPause,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF5350))
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Stop & review
        IconButton(
            onClick = onStop,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PausedContent(
    elapsedSeconds: Int,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit
) {
    // Timer (not pulsing)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = formatDuration(elapsedSeconds),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Paused",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Static waveform
    WaveformBars(isAnimating = false)

    Spacer(modifier = Modifier.height(24.dp))

    // Buttons: Delete | Resume | Stop
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Cancel",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        IconButton(
            onClick = onResume,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF5350))
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Resume",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            onClick = onStop,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StoppedContent(
    elapsedSeconds: Int,
    onSend: () -> Unit,
    onDelete: () -> Unit,
    onReRecord: () -> Unit
) {
    // Duration
    Text(
        text = formatDuration(elapsedSeconds),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Ready to send",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Static waveform
    WaveformBars(isAnimating = false)

    Spacer(modifier = Modifier.height(24.dp))

    // Buttons: Delete | Re-record | Send
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        IconButton(
            onClick = onReRecord,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Re-record",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Send (WhatsApp green)
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF00A884))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PulsingRedDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFFEF5350))
    )
}

@Composable
private fun WaveformBars(isAnimating: Boolean) {
    val barCount = 28
    val barHeights = remember { List(barCount) { Random.nextFloat() * 0.6f + 0.2f } }

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_offset"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barHeights.forEachIndexed { index, baseHeight ->
            val height = if (isAnimating) {
                val phase = (index.toFloat() / barCount + animationOffset) % 1f
                val wave = kotlin.math.sin(phase * Math.PI * 2).toFloat() * 0.3f + 0.5f
                (baseHeight * 0.5f + wave * 0.5f).coerceIn(0.15f, 1f)
            } else {
                baseHeight * 0.5f
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height((height * 36).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isAnimating) Color(0xFFEF5350).copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"
    return "$minutes:$secondsStr"
}
