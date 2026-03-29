package com.dating.home.presentation.chat.chat_detail.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.components.shimmer.shimmerEffect

@Composable
fun MessageSkeleton(
    isLocal: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isLocal) 48.dp else 0.dp,
                end = if (isLocal) 0.dp else 48.dp
            ),
        horizontalArrangement = if (isLocal) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isLocal) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun MessageSkeletonList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MessageSkeleton(isLocal = false)
        MessageSkeleton(isLocal = true)
        MessageSkeleton(isLocal = false)
        MessageSkeleton(isLocal = true)
        MessageSkeleton(isLocal = false)
    }
}
