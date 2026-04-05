package com.dating.home.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

val VerifiedBlue = Color(0xFF1DA1F2)

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = VerifiedBlue
) {
    Icon(
        imageVector = Icons.Default.Verified,
        contentDescription = "Verified profile",
        tint = tint,
        modifier = modifier.size(size)
    )
}
