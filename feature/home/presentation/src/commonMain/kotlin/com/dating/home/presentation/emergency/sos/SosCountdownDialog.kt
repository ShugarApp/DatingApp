package com.dating.home.presentation.emergency.sos

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.sos_cancel
import aura.feature.home.presentation.generated.resources.sos_countdown_message
import aura.feature.home.presentation.generated.resources.sos_countdown_title
import org.jetbrains.compose.resources.stringResource

private val SosRed = Color(0xFFE53935)

@Composable
fun SosCountdownDialog(
    countdown: Int,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = countdown / 5f,
        animationSpec = tween(durationMillis = 900)
    )

    AlertDialog(
        onDismissRequest = onCancel,
        modifier = modifier,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(100.dp),
                        color = SosRed,
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round,
                        trackColor = SosRed.copy(alpha = 0.2f)
                    )
                    Text(
                        text = countdown.toString(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosRed
                    )
                }

                Text(
                    text = stringResource(Res.string.sos_countdown_title, countdown),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SosRed,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(Res.string.sos_countdown_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.sos_cancel),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}
