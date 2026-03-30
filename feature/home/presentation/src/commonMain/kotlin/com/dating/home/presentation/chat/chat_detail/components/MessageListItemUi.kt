package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.util.UiText
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.domain.models.MessageType
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.home.presentation.chat.util.getChatBubbleColorForUser
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageListItemUi(
    messageUi: MessageUi,
    messageWithOpenMenu: MessageUi?,
    onMessageLongClick: (MessageUi) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: (MessageUi.LocalUserMessage) -> Unit,
    onRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onCopyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    highlightText: String? = null
) {
    Box(
        modifier = modifier
    ) {
        when(messageUi) {
            is MessageUi.DateSeparator -> {
                DateSeparatorUi(
                    date = messageUi.date.asString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is MessageUi.LocalUserMessage -> {
                LocalUserMessage(
                    message = messageUi,
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = { onMessageLongClick(messageUi) },
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = { onDeleteClick(messageUi) },
                    onRetryClick = { onRetryClick(messageUi) },
                    onCopyClick = { onCopyClick(messageUi.content) },
                    highlightText = highlightText
                )
            }
            is MessageUi.OtherUserMessage -> {
                OtherUserMessage(
                    message = messageUi,
                    color = getChatBubbleColorForUser(messageUi.sender.id),
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = { onMessageLongClick(messageUi) },
                    onDismissMessageMenu = onDismissMessageMenu,
                    onCopyClick = { onCopyClick(messageUi.content) },
                    highlightText = highlightText
                )
            }
        }
    }
}


@Composable
private fun DateSeparatorUi(
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        DateChip(date = date)
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageUiPreview() {
    AppTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm")
            ),
            messageWithOpenMenu = null,
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onCopyClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
@Preview
fun MessageListItemLocalMessageRetryUiPreview() {
    AppTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                deliveryStatus = ChatMessageDeliveryStatus.FAILED,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm")
            ),
            messageWithOpenMenu = null,
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onCopyClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun MessageListItemOtherMessageUiPreview() {
    AppTheme {
        MessageListItemUi(
            messageUi = MessageUi.OtherUserMessage(
                id = "1",
                content = "Hello world, this is a preview message that spans multiple lines",
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Philipp",
                    initials = "PH"
                )
            ),
            messageWithOpenMenu = null,
            onRetryClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            onDeleteClick = {},
            onCopyClick = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
