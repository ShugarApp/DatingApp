package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aura.core.designsystem.generated.resources.Res as DesignSystemRes
import aura.core.designsystem.generated.resources.arrow_left_icon
import aura.core.designsystem.generated.resources.dots_icon
import aura.core.designsystem.generated.resources.log_out_icon
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.chat_members
import aura.feature.home.presentation.generated.resources.go_back
import aura.feature.home.presentation.generated.resources.leave_chat
import aura.feature.home.presentation.generated.resources.open_chat_options_menu
import aura.feature.home.presentation.generated.resources.users_icon
import com.dating.home.domain.models.ChatMessage
import com.dating.home.domain.models.ChatMessageDeliveryStatus
import com.dating.home.presentation.chat.components.ChatItemHeaderRow
import com.dating.home.presentation.chat.model.ChatUi
import com.dating.core.designsystem.components.avatar.ChatParticipantUi
import com.dating.core.designsystem.components.buttons.ChirpIconButton
import com.dating.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.dating.core.designsystem.components.dropdown.DropDownItem
import com.dating.core.designsystem.components.header.TopAppBarGeneric
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.designsystem.theme.extended
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi?,
    isChatOptionsDropDownOpen: Boolean,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: () -> Unit,
    onManageChatClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChirpIconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                contentDescription = stringResource(Res.string.go_back),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.extended.textSecondary
            )
        }

        if (chatUi != null) {
            val isGroupChat = chatUi.otherParticipants.size > 1
            ChatItemHeaderRow(
                chat = chatUi,
                isGroupChat = isGroupChat,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onManageChatClick()
                    }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            ChirpIconButton(
                onClick = onChatOptionsClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            ChirpDropDownMenu(
                isOpen = isChatOptionsDropDownOpen,
                onDismiss = onDismissChatOptions,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageChatClick
                    ),
                    DropDownItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    ),
                )
            )
        }
    }
}

@Composable
@Preview
fun ChatDetailHeaderPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBarGeneric {
                ChatDetailHeader(
                    isChatOptionsDropDownOpen = true,
                    chatUi = ChatUi(
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
                    ),
                    onChatOptionsClick = {},
                    onManageChatClick = {},
                    onLeaveChatClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                )
            }
        }
    }
}