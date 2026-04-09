package com.dating.home.presentation.home.swipe.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.feed_swipe_like
import aura.feature.home.presentation.generated.resources.feed_swipe_nope
import com.dating.core.designsystem.theme.extended
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun SwipeableCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val swipeThreshold = 300f
    val labelAlpha = (abs(offsetX.value) / swipeThreshold).coerceIn(0f, 1f)
    val labelScale = 0.6f + (labelAlpha * 0.4f)

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = offsetX.value / 22f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            when {
                                offsetX.value > swipeThreshold -> {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = 1800f,
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutLinearInEasing
                                            )
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            targetValue = offsetY.value + 250f,
                                            animationSpec = tween(durationMillis = 350)
                                        )
                                    }
                                    delay(300)
                                    onSwipeRight()
                                }
                                offsetX.value < -swipeThreshold -> {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = -1800f,
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutLinearInEasing
                                            )
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            targetValue = offsetY.value + 250f,
                                            animationSpec = tween(durationMillis = 350)
                                        )
                                    }
                                    delay(300)
                                    onSwipeLeft()
                                }
                                else -> {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
    ) {
        content()

        // Green tint overlay for LIKE
        if (offsetX.value > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF4CAF50).copy(alpha = labelAlpha * 0.22f))
            )
        }

        // Red tint overlay for NOPE
        if (offsetX.value < 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF44336).copy(alpha = labelAlpha * 0.22f))
            )
        }

        // LIKE label (swipe right)
        if (offsetX.value > 0) {
            Text(
                text = stringResource(Res.string.feed_swipe_like),
                color = MaterialTheme.colorScheme.extended.success.copy(alpha = labelAlpha),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(32.dp)
                    .scale(labelScale)
                    .rotate(-20f)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.extended.success.copy(alpha = labelAlpha),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // NOPE label (swipe left)
        if (offsetX.value < 0) {
            Text(
                text = stringResource(Res.string.feed_swipe_nope),
                color = MaterialTheme.colorScheme.error.copy(alpha = labelAlpha),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(32.dp)
                    .scale(labelScale)
                    .rotate(20f)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.error.copy(alpha = labelAlpha),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
