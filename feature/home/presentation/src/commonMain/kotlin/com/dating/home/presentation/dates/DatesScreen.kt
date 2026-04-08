@file:OptIn(ExperimentalMaterial3Api::class)

package com.dating.home.presentation.dates

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.dating.core.designsystem.components.header.MainTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        onFilterSelected = viewModel::selectFilter,
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
    onFilterSelected: (DateFilter) -> Unit,
    onCancelDate: (messageId: String, chatId: String) -> Unit,
    onAcceptDate: (messageId: String, chatId: String) -> Unit,
    onRejectDate: (messageId: String, chatId: String) -> Unit,
    onEditDate: (oldMessageId: String, chatId: String, dateTime: String, location: DateProposalLocation) -> Unit
) {
    Scaffold(
        topBar = {
            MainTopAppBar(title = "My Dates")
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
                EmptyDatesState(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Filter chips
                    FilterChipRow(
                        state = state,
                        onFilterSelected = onFilterSelected
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    if (state.filteredDates.isEmpty()) {
                        EmptyFilterState(
                            filter = state.selectedFilter,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.filteredDates, key = { it.messageId }) { proposal ->
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
    }
}

@Composable
private fun FilterChipRow(
    state: DatesState,
    onFilterSelected: (DateFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(DateFilter.entries) { filter ->
            val count = state.countFor(filter)
            val isSelected = state.selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = if (count > 0) "${filter.label}  $count" else filter.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = statusColor(filter.chipStatus).copy(alpha = 0.18f),
                    selectedLabelColor = statusForegroundColor(filter.chipStatus)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = statusForegroundColor(filter.chipStatus).copy(alpha = 0.4f),
                    selectedBorderWidth = 1.5.dp
                )
            )
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

    val isTerminal = proposal.status == DateProposalStatus.CANCELLED ||
            proposal.status == DateProposalStatus.REJECTED

    val accentColor by animateColorAsState(
        targetValue = statusAccentColor(proposal.status),
        animationSpec = tween(300),
        label = "accentColor"
    )

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
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isTerminal) 0.65f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { showDetail = true }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Status accent stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Text(
                            text = proposal.otherPersonName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val directionLabel = if (proposal.isSentByMe) "You proposed" else "They proposed"
                        Text(
                            text = directionLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DateStatusChip(status = proposal.status)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Date/time row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = formatProposalDateTime(proposal.dateTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Location row
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = proposal.location.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (proposal.location.address.isNotBlank() && proposal.location.address != proposal.location.name) {
                            Text(
                                text = proposal.location.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Action buttons — only for non-terminal statuses
                if (!isTerminal) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    when {
                        proposal.status == DateProposalStatus.PENDING && proposal.isSentByMe -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showEditSheet = true },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { showCancelConfirm = true },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Cancel", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        proposal.status == DateProposalStatus.PENDING && !proposal.isSentByMe -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = { onAcceptDate(proposal.messageId, proposal.chatId) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
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
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Decline", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        proposal.status == DateProposalStatus.ACCEPTED -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { uriHandler.openUri(mapsUrl) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Map, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Maps", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { onNavigateToChatDetail(proposal.chatId) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Message, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Chat", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = { showCancelConfirm = true },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
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
                } else {
                    // Terminal state: just a Chat button
                    OutlinedButton(
                        onClick = { onNavigateToChatDetail(proposal.chatId) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Message, null, Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("View Chat", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateStatusChip(status: DateProposalStatus) {
    val bg = statusColor(status)
    val fg = statusForegroundColor(status)
    val label = when (status) {
        DateProposalStatus.PENDING -> "Pending"
        DateProposalStatus.ACCEPTED -> "Accepted"
        DateProposalStatus.CANCELLED -> "Cancelled"
        DateProposalStatus.REJECTED -> "Declined"
        DateProposalStatus.EDITED -> "Edited"
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun EmptyDatesState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No dates yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Propose a date in a chat and it will appear here once accepted.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyFilterState(filter: DateFilter, modifier: Modifier = Modifier) {
    val message = when (filter) {
        DateFilter.UPCOMING -> "No upcoming dates. Propose a date in a chat!"
        DateFilter.PENDING -> "No pending proposals right now."
        DateFilter.ACCEPTED -> "No accepted dates yet."
        DateFilter.CANCELLED -> "No cancelled dates."
        DateFilter.REJECTED -> "No declined proposals."
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Color helpers ─────────────────────────────────────────────────────────────

@Composable
private fun statusAccentColor(status: DateProposalStatus): Color = when (status) {
    DateProposalStatus.PENDING -> Color(0xFFFFC107)
    DateProposalStatus.ACCEPTED -> Color(0xFF4CAF50)
    DateProposalStatus.CANCELLED -> MaterialTheme.colorScheme.error
    DateProposalStatus.REJECTED -> MaterialTheme.colorScheme.outline
    DateProposalStatus.EDITED -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun statusColor(status: DateProposalStatus?): Color = when (status) {
    DateProposalStatus.PENDING -> Color(0xFFFFF3CD)
    DateProposalStatus.ACCEPTED -> Color(0xFFD4EDDA)
    DateProposalStatus.CANCELLED -> Color(0xFFFFEBEE)
    DateProposalStatus.REJECTED -> Color(0xFFEEEEEE)
    DateProposalStatus.EDITED -> Color(0xFFEDE7F6)
    null -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun statusForegroundColor(status: DateProposalStatus?): Color = when (status) {
    DateProposalStatus.PENDING -> Color(0xFF856404)
    DateProposalStatus.ACCEPTED -> Color(0xFF155724)
    DateProposalStatus.CANCELLED -> Color(0xFFB71C1C)
    DateProposalStatus.REJECTED -> Color(0xFF424242)
    DateProposalStatus.EDITED -> Color(0xFF4527A0)
    null -> MaterialTheme.colorScheme.onSurfaceVariant
}

/** Maps domain model to the UI model expected by DateProposalDetailSheet / buildMapsUrl */
private fun AcceptedDateProposal.toUi() = DateProposalUi(
    messageId = messageId,
    dateTime = dateTime,
    location = location,
    status = status,
    canAccept = status == DateProposalStatus.PENDING && !isSentByMe,
    canReject = status == DateProposalStatus.PENDING && !isSentByMe,
    canCancel = status == DateProposalStatus.PENDING || status == DateProposalStatus.ACCEPTED,
    canEdit = status == DateProposalStatus.PENDING && isSentByMe
)
