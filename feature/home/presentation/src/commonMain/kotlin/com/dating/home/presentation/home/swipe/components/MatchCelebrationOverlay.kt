package com.dating.home.presentation.home.swipe.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.match_celebration_keep_swiping
import aura.feature.home.presentation.generated.resources.match_celebration_send_message
import aura.feature.home.presentation.generated.resources.match_celebration_subtitle
import aura.feature.home.presentation.generated.resources.match_celebration_title
import coil3.compose.AsyncImage
import com.dating.core.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchCelebrationOverlay(
    currentUserPhotoUrl: String?,
    matchedUserName: String,
    matchedUserPhotoUrl: String?,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photosScale = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonsAlpha = remember { Animatable(0f) }
    val heartPulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        photosScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
        buttonsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, delayMillis = 150)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            heartPulse.animateTo(1.25f, animationSpec = tween(600))
            heartPulse.animateTo(1f, animationSpec = tween(600))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0518),
                        Color(0xFF3D0D60),
                        Color(0xFF0A0518)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Central radial glow
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE91E8C).copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Photos with overlapping layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .scale(photosScale.value),
                contentAlignment = Alignment.Center
            ) {
                // Glow behind left photo
                Box(
                    modifier = Modifier
                        .offset(x = (-60).dp)
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFE91E8C).copy(alpha = 0.35f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                // Glow behind right photo
                Box(
                    modifier = Modifier
                        .offset(x = 60.dp)
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFF6B9D).copy(alpha = 0.35f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                // Current user photo (left, tilted)
                Box(modifier = Modifier.offset(x = (-58).dp).rotate(-8f)) {
                    MatchAvatarCircle(
                        photoUrl = currentUserPhotoUrl,
                        borderColor = Color(0xFFE91E8C)
                    )
                }
                // Matched user photo (right, tilted)
                Box(modifier = Modifier.offset(x = 58.dp).rotate(8f)) {
                    MatchAvatarCircle(
                        photoUrl = matchedUserPhotoUrl,
                        borderColor = Color(0xFFFF6B9D)
                    )
                }
                // Pulsing heart badge at bottom center
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp)
                        .scale(heartPulse.value)
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFF6B9D), Color(0xFFE91E8C))
                            ),
                            CircleShape
                        )
                        .border(3.dp, Color(0xFF0A0518), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title + subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha.value)
            ) {
                Text(
                    text = stringResource(Res.string.match_celebration_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF6B9D), Color(0xFFFFB347))
                        )
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.match_celebration_subtitle, matchedUserName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha.value)
            ) {
                // Gradient send message button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFE91E8C), Color(0xFFFF6B9D))
                            ),
                            RoundedCornerShape(28.dp)
                        )
                ) {
                    Button(
                        onClick = onSendMessage,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.match_celebration_send_message),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                TextButton(onClick = onKeepSwiping) {
                    Text(
                        text = stringResource(Res.string.match_celebration_keep_swiping),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchAvatarCircle(
    photoUrl: String?,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .border(width = 3.dp, color = borderColor, shape = CircleShape)
            .padding(3.dp)
            .clip(CircleShape)
            .background(Color(0xFF2A1040))
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2A1040)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MatchCelebrationOverlayPreview() {
    AppTheme {
        MatchCelebrationOverlay(
            currentUserPhotoUrl = null,
            matchedUserName = "Sofia",
            matchedUserPhotoUrl = null,
            onSendMessage = {},
            onKeepSwiping = {}
        )
    }
}
