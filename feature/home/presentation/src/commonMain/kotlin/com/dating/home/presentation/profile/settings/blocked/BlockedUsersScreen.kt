package com.dating.home.presentation.profile.settings.blocked

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.blocked_users_title
import aura.feature.home.presentation.generated.resources.blocked_users_empty
import aura.feature.home.presentation.generated.resources.blocked_users_unblock
import aura.feature.home.presentation.generated.resources.blocked_users_unblock_title
import aura.feature.home.presentation.generated.resources.blocked_users_unblock_desc
import aura.feature.home.presentation.generated.resources.blocked_users_deleted_user
import aura.feature.home.presentation.generated.resources.cancel
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.home.domain.block.BlockedUser
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BlockedUsersScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlockedUsersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BlockedUsersEvent.ShowToast -> {
                snackbarHostState.showSnackbar(event.message.asStringAsync())
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.blocked_users_title),
                onBack = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.blockedUsers.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(Res.string.blocked_users_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        items = state.blockedUsers,
                        key = { it.userId }
                    ) { user ->
                        BlockedUserItem(
                            user = user,
                            onUnblockClick = {
                                viewModel.onAction(BlockedUsersAction.OnUnblockClick(user))
                            }
                        )
                    }
                }
            }
        }

        if (state.showUnblockDialog && state.userToUnblock != null) {
            val username = state.userToUnblock?.username
                ?: stringResource(Res.string.blocked_users_deleted_user)
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.blocked_users_unblock_title, username),
                description = stringResource(Res.string.blocked_users_unblock_desc),
                confirmButtonText = stringResource(Res.string.blocked_users_unblock),
                cancelButtonText = stringResource(Res.string.cancel),
                onConfirmClick = { viewModel.onAction(BlockedUsersAction.OnConfirmUnblock) },
                onCancelClick = { viewModel.onAction(BlockedUsersAction.OnDismissUnblockDialog) },
                onDismiss = { viewModel.onAction(BlockedUsersAction.OnDismissUnblockDialog) }
            )
        }
    }
}

@Composable
private fun BlockedUserItem(
    user: BlockedUser,
    onUnblockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (user.profilePictureUrl != null) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.username ?: stringResource(Res.string.blocked_users_deleted_user),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
        }

        ChirpButton(
            text = stringResource(Res.string.blocked_users_unblock),
            onClick = onUnblockClick,
            style = AppButtonStyle.SECONDARY
        )
    }
}
