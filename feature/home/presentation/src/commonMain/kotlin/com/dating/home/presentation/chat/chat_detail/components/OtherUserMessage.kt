package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.copy
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.core.designsystem.components.chat.ChirpChatBubble
import com.dating.core.designsystem.components.chat.TrianglePosition
import com.dating.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.dating.core.designsystem.components.dropdown.DropDownItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun OtherUserMessage(
    message: MessageUi.OtherUserMessage,
    color: Color,
    messageWithOpenMenu: MessageUi?,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlightText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 48.dp)
            .semantics {
                contentDescription = "Message from ${message.sender.username}: ${message.content}"
            },
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChirpAvatarPhoto(
            displayText = message.sender.initials,
            imageUrl = message.sender.imageUrl,
            contentDescription = "Profile photo of ${message.sender.username}"
        )
        Box {
            ChirpChatBubble(
                messageContent = message.content,
                sender = message.sender.username,
                trianglePosition = TrianglePosition.LEFT,
                color = color,
                formattedDateTime = message.formattedSentTime.asString(),
                highlightText = highlightText,
                onLongClick = onMessageLongClick
            )

            ChirpDropDownMenu(
                isOpen = message.id == messageWithOpenMenu?.id,
                onDismiss = onDismissMessageMenu,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.copy),
                        icon = Icons.Default.ContentCopy,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        onClick = onCopyClick
                    ),
                )
            )
        }
    }
}
