package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.theme.extended
import com.dating.home.domain.models.ReactionSummary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReactionRow(
    reactions: List<ReactionSummary>,
    onReactionTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reactions.isEmpty()) return

    FlowRow(
        modifier = modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        reactions.forEach { reaction ->
            val shape = RoundedCornerShape(12.dp)
            Text(
                text = "${reaction.emoji} ${reaction.count}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .clip(shape)
                    .then(
                        if (reaction.reactedByMe) {
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = shape
                                )
                        } else {
                            Modifier.background(
                                MaterialTheme.colorScheme.extended.surfaceHigher,
                                shape
                            )
                        }
                    )
                    .clickable { onReactionTapped(reaction.emoji) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
