package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dating.home.domain.models.DateProposalStatus
import com.dating.home.presentation.chat.model.DateProposalUi

@Composable
fun DateProposalBubbleContent(
    proposal: DateProposalUi,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Date Proposal",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Date/Time row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = formatProposalDateTime(proposal.dateTime),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Location row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = proposal.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Status chip
        ProposalStatusChip(status = proposal.status)

        // Action buttons (only if proposal is PENDING)
        if (proposal.canAccept || proposal.canReject || proposal.canCancel || proposal.canEdit) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (proposal.canAccept) {
                    FilledTonalButton(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (proposal.canReject) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (proposal.canEdit) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (proposal.canCancel) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposalStatusChip(
    status: DateProposalStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        DateProposalStatus.PENDING -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF856404),
            "Pending"
        )
        DateProposalStatus.ACCEPTED -> Triple(
            Color(0xFFD4EDDA),
            Color(0xFF155724),
            "Accepted"
        )
        DateProposalStatus.REJECTED -> Triple(
            Color(0xFFF8D7DA),
            Color(0xFF721C24),
            "Rejected"
        )
        DateProposalStatus.CANCELLED -> Triple(
            Color(0xFFE2E3E5),
            Color(0xFF383D41),
            "Cancelled"
        )
        DateProposalStatus.EDITED -> Triple(
            Color(0xFFCCE5FF),
            Color(0xFF004085),
            "Edited"
        )
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

private fun formatProposalDateTime(isoDateTime: String): String {
    return try {
        // Parse ISO-8601 and format for display
        val parts = isoDateTime.split("T")
        if (parts.size == 2) {
            val datePart = parts[0] // "2026-04-10"
            val timePart = parts[1].take(5) // "19:30"
            "$datePart at $timePart"
        } else {
            isoDateTime
        }
    } catch (_: Exception) {
        isoDateTime
    }
}
