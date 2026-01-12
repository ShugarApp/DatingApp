package com.dating.chat.presentation.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aura.core.designsystem.generated.resources.Res as DesignSystemRes
import aura.core.designsystem.generated.resources.logo_chirp
import com.dating.chat.presentation.components.ChatHeader
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatListHeader(
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_chirp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Chats",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatListHeaderPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ChatListHeader()
        }
    }
}