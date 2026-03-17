package com.dating.home.presentation.home.swipe.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.feed_swipe_like
import shugar.feature.home.presentation.generated.resources.feed_swipe_nope
import com.dating.core.designsystem.theme.extended
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
    var offset by remember { mutableStateOf(Offset.Zero) }
    var dismissRight by remember { mutableStateOf(false) }
    var dismissLeft by remember { mutableStateOf(false) }

    val swipeThreshold = 300f
    val labelAlpha = (abs(offset.x) / swipeThreshold).coerceIn(0f, 1f)

    if (dismissRight) {
        onSwipeRight()
        dismissRight = false
        offset = Offset.Zero
    } else if (dismissLeft) {
        onSwipeLeft()
        dismissLeft = false
        offset = Offset.Zero
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .graphicsLayer {
                rotationZ = offset.x / 20f
                alpha = 1f - (abs(offset.x) / 1000f).coerceIn(0f, 0.5f)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (offset.x > swipeThreshold) {
                            dismissRight = true
                        } else if (offset.x < -swipeThreshold) {
                            dismissLeft = true
                        } else {
                            offset = Offset.Zero
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    }
                )
            }
    ) {
        content()

        // LIKE label (swipe right)
        if (offset.x > 0) {
            Text(
                text = stringResource(Res.string.feed_swipe_like),
                color = MaterialTheme.colorScheme.extended.success.copy(alpha = labelAlpha),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(32.dp)
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
        if (offset.x < 0) {
            Text(
                text = stringResource(Res.string.feed_swipe_nope),
                color = MaterialTheme.colorScheme.error.copy(alpha = labelAlpha),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(32.dp)
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
