@file:OptIn(ExperimentalMaterial3Api::class)

package com.dating.home.presentation.dates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.dating.home.domain.models.AcceptedDateProposal
import com.dating.home.domain.models.DateProposalLocation
import com.dating.home.domain.models.DateProposalStatus
import com.dating.home.presentation.chat.chat_detail.components.DateProposalDetailSheet
import com.dating.home.presentation.chat.chat_detail.components.DateProposalSheet
import com.dating.home.presentation.chat.chat_detail.components.buildMapsUrl
import com.dating.home.presentation.chat.chat_detail.components.formatProposalDateTime
import com.dating.home.presentation.chat.model.DateProposalUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatesRoot(
    onNavigateToChatDetail: (chatId: String) -> Unit,
    viewModel: DatesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DatesScreen(
        state = state,
        onNavigateToChatDetail = onNavigateToChatDetail,
        onCancelDate = viewModel::cancelDate,
        onAcceptDate = viewModel::acceptDate,
        onRejectDate = viewModel::rejectDate,
        onEditDate = viewModel::editDate
    )
}

@Composable
private fun DatesScreen(
    state: DatesState,
    onNavigateToChatDetail: (chatId: String) -> Unit,
    onCancelDate: (messageId: String, chatId: String) -> Unit,
    onAcceptDate: (messageId: String, chatId: String) -> Unit,
    onRejectDate: (messageId: String, chatId: String) -> Unit,
    onEditDate: (oldMessageId: String, chatId: String, dateTime: String, location: DateProposalLocation) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Dates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            state.dates.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No dates yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Propose or accept a date in chat to see it here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.dates, key = { it.messageId }) { proposal ->
                        DateCard(
                            proposal = proposal,
                            onNavigateToChatDetail = onNavigateToChatDetail,
                            onCancelDate = onCancelDate,
                            onAcceptDate = onAcceptDate,
                            onRejectDate = onRejectDate,
                            onEditDate = onEditDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCard(
    proposal: AcceptedDateProposal,
    onNavigateToChatDetail: (chatId: String) -> Unit,
    onCancelDate: (messageId: String, chatId: String) -> Unit,
    onAcceptDate: (messageId: String, chatId: String) -> Unit,
    onRejectDate: (messageId: String, chatId: String) -> Unit,
    onEditDate: (oldMessageId: String, chatId: String, dateTime: String, location: DateProposalLocation) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var showDetail by remember { mutableStateOf(false) }
    var showCancelConfirm by remember { mutableStateOf(false) }
    var showRejectConfirm by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    // Full detail sheet
    if (showDetail) {
        DateProposalDetailSheet(
            proposal = proposal.toUi(),
            onDismiss = { showDetail = false },
            onAccept = { onAcceptDate(proposal.messageId, proposal.chatId); showDetail = false },
            onReject = { showDetail = false; showRejectConfirm = true },
            onCancel = { showDetail = false; showCancelConfirm = true },
            onEdit = { showDetail = false; showEditSheet = true }
        )
    }

    // Edit sheet
    if (showEditSheet) {
        DateProposalSheet(
            onDismiss = { showEditSheet = false },
            onSubmit = { newDateTime, newLocation ->
                onEditDate(proposal.messageId, proposal.chatId, newDateTime, newLocation)
                showEditSheet = false
            },
            initialDateTime = proposal.dateTime,
            initialLocation = proposal.location,
            isEditing = true
        )
    }

    // Cancel confirmation
    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("Cancel Date") },
            text = { Text("Are you sure you want to cancel this date with ${proposal.otherPersonName}?") },
            confirmButton = {
                TextButton(
                    onClick = { onCancelDate(proposal.messageId, proposal.chatId); showCancelConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancel Date") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) { Text("Keep") }
            }
        )
    }

    // Reject confirmation
    if (showRejectConfirm) {
        AlertDialog(
            onDismissRequest = { showRejectConfirm = false },
            title = { Text("Reject Proposal") },
            text = { Text("Reject this date proposal from ${proposal.otherPersonName}?") },
            confirmButton = {
                TextButton(
                    onClick = { onRejectDate(proposal.messageId, proposal.chatId); showRejectConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Reject") }
            },
            dismissButton = {
                TextButton(onClick = { showRejectConfirm = false }) { Text("Keep") }
            }
        )
    }

    val mapsUrl = buildMapsUrl(proposal.toUi())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { showDetail = true }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: avatar + name + status chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChirpAvatarPhoto(
                    displayText = proposal.otherPersonName.take(2).uppercase(),
                    imageUrl = proposal.otherPersonAvatarUrl,
                    contentDescription = proposal.otherPersonName
                )
                Column(modifier = Modifier.weight(1f)) {
                    val prefix = if (proposal.isSentByMe) "To" else "From"
                    Text(
                        text = "$prefix ${proposal.otherPersonName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap to view full details",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DateStatusChip(status = proposal.status)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Date/time
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CalendarMonth, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(formatProposalDateTime(proposal.dateTime), style = MaterialTheme.typography.bodyMedium)
            }

            // Location
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(proposal.location.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    if (proposal.location.address.isNotBlank() && proposal.location.address != proposal.location.name) {
                        Text(proposal.location.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Action buttons — differ by status + role
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            when {
                // I sent a PENDING proposal — I can edit or cancel
                proposal.status == DateProposalStatus.PENDING && proposal.isSentByMe -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showEditSheet = true }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Edit, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { showCancelConfirm = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                // I received a PENDING proposal — I can accept or reject
                proposal.status == DateProposalStatus.PENDING && !proposal.isSentByMe -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { onAcceptDate(proposal.messageId, proposal.chatId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Check, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Accept", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { showRejectConfirm = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reject", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                // ACCEPTED — Maps, Chat, Edit (sender only), Cancel
                proposal.status == DateProposalStatus.ACCEPTED -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { uriHandler.openUri(mapsUrl) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Map, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Maps", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(onClick = { onNavigateToChatDetail(proposal.chatId) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Message, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Chat", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { showCancelConfirm = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun DateStatusChip(status: DateProposalStatus) {
    val (bg, fg, label) = when (status) {
        DateProposalStatus.PENDING -> Triple(Color(0xFFFFF3CD), Color(0xFF856404), "Pending")
        DateProposalStatus.ACCEPTED -> Triple(Color(0xFFD4EDDA), Color(0xFF155724), "Accepted")
        else -> Triple(Color(0xFFE2E3E5), Color(0xFF383D41), status.name.lowercase().replaceFirstChar { it.uppercase() })
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/** Maps domain model to the UI model expected by DateProposalDetailSheet / buildMapsUrl */
private fun AcceptedDateProposal.toUi() = DateProposalUi(
    messageId = messageId,
    dateTime = dateTime,
    location = location,
    status = status,
    canAccept = status == DateProposalStatus.PENDING && !isSentByMe,
    canReject = status == DateProposalStatus.PENDING && !isSentByMe,
    canCancel = true,
    canEdit = isSentByMe
)
