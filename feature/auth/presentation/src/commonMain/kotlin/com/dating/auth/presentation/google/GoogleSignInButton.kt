package com.dating.auth.presentation.google

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    ChirpButton(
        text = "Continuar con Google",
        onClick = onClick,
        modifier = modifier,
        style = AppButtonStyle.SECONDARY,
        enabled = enabled,
        isLoading = isLoading,
        leadingIcon = {
            GoogleIcon(modifier = Modifier.size(18.dp))
        }
    )
}

@Composable
private fun GoogleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawGoogleG()
    }
}

private fun DrawScope.drawGoogleG() {
    val w = size.width
    val h = size.height

    val blue = Color(0xFF4285F4)
    val green = Color(0xFF34A853)
    val yellow = Color(0xFFFBBC05)
    val red = Color(0xFFEA4335)

    val cx = w / 2f
    val cy = h / 2f
    val outerR = w * 0.48f
    val innerR = outerR * 0.58f

    val arcSize = Size(outerR * 2, outerR * 2)
    val arcTopLeft = Offset(cx - outerR, cy - outerR)

    // Blue arc: right side (from -45 to 45 degrees, i.e. -45 start, 90 sweep)
    drawArc(color = blue, startAngle = -45f, sweepAngle = 90f, useCenter = true, topLeft = arcTopLeft, size = arcSize)
    // Green arc: bottom (45 to 135)
    drawArc(color = green, startAngle = 45f, sweepAngle = 90f, useCenter = true, topLeft = arcTopLeft, size = arcSize)
    // Yellow arc: left (135 to 225)
    drawArc(color = yellow, startAngle = 135f, sweepAngle = 90f, useCenter = true, topLeft = arcTopLeft, size = arcSize)
    // Red arc: top (225 to 315 = -45)
    drawArc(color = red, startAngle = 225f, sweepAngle = 90f, useCenter = true, topLeft = arcTopLeft, size = arcSize)

    // White center to make the ring
    drawCircle(color = Color.White, radius = innerR, center = Offset(cx, cy))

    // Blue horizontal bar (right half of the G)
    val barH = outerR * 0.36f
    drawRect(
        color = blue,
        topLeft = Offset(cx, cy - barH / 2f),
        size = Size(outerR, barH)
    )

    // Cut out top-right quadrant between inner circle and bar to open the G
    val cutout = Path().apply {
        moveTo(cx, cy - innerR)
        lineTo(cx + outerR + 1f, cy - innerR)
        lineTo(cx + outerR + 1f, cy - barH / 2f)
        lineTo(cx, cy - barH / 2f)
        close()
    }
    drawPath(cutout, color = Color.White)
}
