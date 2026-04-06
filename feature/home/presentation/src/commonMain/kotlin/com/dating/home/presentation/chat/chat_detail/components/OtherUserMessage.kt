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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.copy
import com.dating.home.domain.models.MessageType
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
    onReactionTapped: (String) -> Unit = {},
    onDoubleTapReact: () -> Unit = {},
    onAcceptProposal: () -> Unit = {},
    onRejectProposal: () -> Unit = {},
    onEditProposal: () -> Unit = {},
    modifier: Modifier = Modifier,
    highlightText: String? = null
) {
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }
    var showProposalDetail by remember { mutableStateOf(false) }

    if (showProposalDetail && message.dateProposal != null) {
        DateProposalDetailSheet(
            proposal = message.dateProposal,
            onDismiss = { showProposalDetail = false },
            onAccept = { onAcceptProposal(); showProposalDetail = false },
            onReject = { onRejectProposal(); showProposalDetail = false },
            onCancel = {},
            onEdit = { onEditProposal(); showProposalDetail = false }
        )
    }

    fullScreenImageUrl?.let { url ->
        FullScreenImageViewer(
            imageUrl = url,
            onDismiss = { fullScreenImageUrl = null }
        )
    }

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
                onLongClick = onMessageLongClick,
                onDoubleClick = onDoubleTapReact,
                mediaContent = when {
                    message.messageType == MessageType.DATE_PROPOSAL && message.dateProposal != null -> {
                        {
                            DateProposalBubbleContent(
                                proposal = message.dateProposal,
                                onAccept = onAcceptProposal,
                                onReject = onRejectProposal,
                                onCancel = {},
                                onEdit = onEditProposal,
                                onViewDetail = { showProposalDetail = true }
                            )
                        }
                    }
                    message.messageType == MessageType.LOCATION && message.locationMessage != null -> {
                        {
                            LocationBubbleContent(location = message.locationMessage)
                        }
                    }
                    message.messageType != MessageType.TEXT -> {
                        {
                            MediaMessageContent(
                                content = message.content,
                                messageType = message.messageType,
                                onImageClick = if (message.messageType == MessageType.IMAGE) {
                                    { fullScreenImageUrl = message.content }
                                } else null
                            )
                        }
                    }
                    else -> null
                },
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
                )
            )
        }
    }
}
