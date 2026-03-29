package com.dating.home.presentation.chat.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.presentation.chat.components.ChatItemHeaderRow
import com.dating.home.presentation.chat.model.ChatUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import kotlin.time.Clock
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val isGroupChat = chat.otherParticipants.size > 1
    val chatName = chat.otherParticipants.joinToString(", ") { it.username }
    val lastMessagePreview = chat.lastMessage?.content ?: ""
    Card(
        modifier = modifier.semantics {
            contentDescription = "Chat with $chatName. $lastMessagePreview"
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ChatItemHeaderRow(
                chat = chat,
                isGroupChat = isGroupChat,
                modifier = Modifier
                    .fillMaxWidth()
            )

            if (chat.lastMessage != null) {
                val previewMessage = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                        )
                    ) {
                        if (chat.lastMessageSenderUsername != null) {
                            append(chat.lastMessageSenderUsername + ": ")
                        }
                    }
                    append(chat.lastMessage.content)
                }
                Text(
                    text = previewMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
@Preview
fun ChatListItemUiPreview() {
    AppTheme(darkTheme = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            ChatListItemUi(
                isSelected = true,
                modifier = Modifier.fillMaxWidth(),
                chat = ChatUi(
                    id = "1",
                    localParticipant = ChatParticipantUi(
                        id = "1",
                        username = "Philipp",
                        initials = "PH",
                    ),
                    otherParticipants = listOf(
                        ChatParticipantUi(
                            id = "2",
                            username = "Cinderella",
                            initials = "CI",
                        ),
                        ChatParticipantUi(
                            id = "3",
                            username = "Josh",
                            initials = "JO",
                        )
                    ),
                    lastMessage = ChatMessage(
                        id = "1",
                        chatId = "1",
                        content = "This is a last chat message that was sent by Philipp " +
                                "and goes over multiple lines to showcase the ellipsis",
                        createdAt = Clock.System.now(),
                        senderId = "1",
                        deliveryStatus = ChatMessageDeliveryStatus.SENT
                    ),
                    lastMessageSenderUsername = "Philipp"
                )
            )
            ChatListItemUi(
                isSelected = false,
                modifier = Modifier.fillMaxWidth(),
                chat = ChatUi(
                    id = "2",
                    localParticipant = ChatParticipantUi(
                        id = "1",
                        username = "Philipp",
                        initials = "PH",
                    ),
                    otherParticipants = listOf(
                        ChatParticipantUi(
                            id = "4",
                            username = "Sarah",
                            initials = "SA",
                        )
                    ),
                    lastMessage = ChatMessage(
                        id = "2",
                        chatId = "2",
                        content = "Hey! How are you?",
                        createdAt = Clock.System.now(),
                        senderId = "4",
                        deliveryStatus = ChatMessageDeliveryStatus.SENT
                    ),
                    lastMessageSenderUsername = "Sarah"
                )
            )
        }
    }
}