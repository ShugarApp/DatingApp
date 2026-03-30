package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.copy
import shugar.feature.home.presentation.generated.resources.delete_for_everyone
import shugar.feature.home.presentation.generated.resources.reload_icon
import shugar.feature.home.presentation.generated.resources.retry
import shugar.feature.home.presentation.generated.resources.you
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.MessageType
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.core.designsystem.components.chat.ChirpChatBubble
import com.dating.core.designsystem.components.chat.TrianglePosition
import com.dating.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.dating.core.designsystem.components.dropdown.DropDownItem
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun LocalUserMessage(
    message: MessageUi.LocalUserMessage,
    messageWithOpenMenu: MessageUi?,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit,
    onCopyClick: () -> Unit,
    onReactionTapped: (String) -> Unit = {},
    onDoubleTapReact: () -> Unit = {},
    modifier: Modifier = Modifier,
    highlightText: String? = null
) {
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    fullScreenImageUrl?.let { url ->
        FullScreenImageViewer(
            imageUrl = url,
            onDismiss = { fullScreenImageUrl = null }
        )
    }

    val statusText = when (message.deliveryStatus) {
        ChatMessageDeliveryStatus.SENDING -> "sending"
        ChatMessageDeliveryStatus.SENT -> "sent"
        ChatMessageDeliveryStatus.READ -> "read"
        ChatMessageDeliveryStatus.FAILED -> "failed"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp)
            .semantics {
                contentDescription = "Your message: ${message.content}, status: $statusText"
                if (message.deliveryStatus == ChatMessageDeliveryStatus.FAILED) {
                    liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
                    stateDescription = "Message failed to send"
                }
            },
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        if (message.deliveryStatus == ChatMessageDeliveryStatus.FAILED) {
            IconButton(
                onClick = onRetryClick
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.reload_icon),
                    contentDescription = stringResource(Res.string.retry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Box {
            ChirpChatBubble(
                messageContent = message.content,
                sender = stringResource(Res.string.you),
                formattedDateTime = message.formattedSentTime.asString(),
                trianglePosition = TrianglePosition.RIGHT,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                highlightText = highlightText,
                messageStatus = {
                    MessageStatus(
                        status = message.deliveryStatus
                    )
                },
                onLongClick = {
                    onMessageLongClick()
                },
                onDoubleClick = onDoubleTapReact,
                mediaContent = if (message.messageType != MessageType.TEXT) {
                    {
                        MediaMessageContent(
                            content = message.content,
                            messageType = message.messageType,
                            onImageClick = if (message.messageType == MessageType.IMAGE) {
                                { fullScreenImageUrl = message.content }
                            } else null
                        )
                    }
                } else null,
                reactionContent = if (message.reactions.isNotEmpty()) {
                    {
                        ReactionRow(
                            reactions = message.reactions,
                            onReactionTapped = onReactionTapped
                        )
                    }
                } else null
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
                    DropDownItem(
                        title = stringResource(Res.string.delete_for_everyone),
                        icon = Icons.Default.Delete,
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onDeleteClick
                    ),
                )
            )
        }
    }
}
