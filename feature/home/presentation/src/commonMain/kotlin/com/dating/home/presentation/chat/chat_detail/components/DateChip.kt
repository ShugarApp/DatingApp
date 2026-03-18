package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DateChip(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
    ) {
        Text(
            text = date,
            modifier = Modifier
                .padding(
                    vertical = 6.dp,
                    horizontal = 16.dp
                ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}