package com.dating.home.presentation.profile.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.date_safety_all_set
import aura.feature.home.presentation.generated.resources.date_safety_check_battery
import aura.feature.home.presentation.generated.resources.date_safety_check_battery_desc
import aura.feature.home.presentation.generated.resources.date_safety_check_public
import aura.feature.home.presentation.generated.resources.date_safety_check_public_desc
import aura.feature.home.presentation.generated.resources.date_safety_check_someone_knows
import aura.feature.home.presentation.generated.resources.date_safety_check_someone_knows_desc
import aura.feature.home.presentation.generated.resources.date_safety_check_sos
import aura.feature.home.presentation.generated.resources.date_safety_check_sos_desc
import aura.feature.home.presentation.generated.resources.date_safety_check_transport
import aura.feature.home.presentation.generated.resources.date_safety_check_transport_desc
import aura.feature.home.presentation.generated.resources.date_safety_checklist_progress
import aura.feature.home.presentation.generated.resources.date_safety_checklist_section
import aura.feature.home.presentation.generated.resources.date_safety_tip_1_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_1_title
import aura.feature.home.presentation.generated.resources.date_safety_tip_2_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_2_title
import aura.feature.home.presentation.generated.resources.date_safety_tip_3_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_3_title
import aura.feature.home.presentation.generated.resources.date_safety_tip_4_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_4_title
import aura.feature.home.presentation.generated.resources.date_safety_tip_5_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_5_title
import aura.feature.home.presentation.generated.resources.date_safety_tip_6_desc
import aura.feature.home.presentation.generated.resources.date_safety_tip_6_title
import aura.feature.home.presentation.generated.resources.date_safety_tips_section
import aura.feature.home.presentation.generated.resources.date_safety_tips_subtitle
import aura.feature.home.presentation.generated.resources.date_safety_tips_title
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────
// Screen 1: Consejos de seguridad
// ─────────────────────────────────────────────

@Composable
fun SafetyTipsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.date_safety_tips_section),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(Res.string.date_safety_tips_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(Res.string.date_safety_tips_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccessCardList(title = stringResource(Res.string.date_safety_tips_section)) {
                SafetyTipRow(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(Res.string.date_safety_tip_1_title),
                    desc = stringResource(Res.string.date_safety_tip_1_desc)
                )
                SafetyTipRow(
                    icon = Icons.Default.Groups,
                    title = stringResource(Res.string.date_safety_tip_2_title),
                    desc = stringResource(Res.string.date_safety_tip_2_desc)
                )
                SafetyTipRow(
                    icon = Icons.Default.DirectionsCar,
                    title = stringResource(Res.string.date_safety_tip_3_title),
                    desc = stringResource(Res.string.date_safety_tip_3_desc)
                )
                SafetyTipRow(
                    icon = Icons.Default.Favorite,
                    title = stringResource(Res.string.date_safety_tip_4_title),
                    desc = stringResource(Res.string.date_safety_tip_4_desc)
                )
                SafetyTipRow(
                    icon = Icons.Default.Lock,
                    title = stringResource(Res.string.date_safety_tip_5_title),
                    desc = stringResource(Res.string.date_safety_tip_5_desc)
                )
                SafetyTipRow(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(Res.string.date_safety_tip_6_title),
                    desc = stringResource(Res.string.date_safety_tip_6_desc)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Screen 2: Checklist antes de salir
// ─────────────────────────────────────────────

@Composable
fun DateSafetyChecklistScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checklist = remember { mutableStateListOf(false, false, false, false, false) }
    val completedCount = checklist.count { it }
    val allDone = completedCount == checklist.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.date_safety_checklist_section),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (allDone) {
                            stringResource(Res.string.date_safety_all_set)
                        } else {
                            stringResource(
                                Res.string.date_safety_checklist_progress,
                                completedCount,
                                checklist.size
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (allDone) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (allDone) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { completedCount.toFloat() / checklist.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccessCardList(title = stringResource(Res.string.date_safety_checklist_section)) {
                ChecklistRow(
                    checked = checklist[0],
                    title = stringResource(Res.string.date_safety_check_public),
                    subtitle = stringResource(Res.string.date_safety_check_public_desc),
                    icon = Icons.Default.LocationOn,
                    onToggle = { checklist[0] = !checklist[0] }
                )
                ChecklistRow(
                    checked = checklist[1],
                    title = stringResource(Res.string.date_safety_check_someone_knows),
                    subtitle = stringResource(Res.string.date_safety_check_someone_knows_desc),
                    icon = Icons.Default.ContactPhone,
                    onToggle = { checklist[1] = !checklist[1] }
                )
                ChecklistRow(
                    checked = checklist[2],
                    title = stringResource(Res.string.date_safety_check_battery),
                    subtitle = stringResource(Res.string.date_safety_check_battery_desc),
                    icon = Icons.Default.PhoneAndroid,
                    onToggle = { checklist[2] = !checklist[2] }
                )
                ChecklistRow(
                    checked = checklist[3],
                    title = stringResource(Res.string.date_safety_check_transport),
                    subtitle = stringResource(Res.string.date_safety_check_transport_desc),
                    icon = Icons.Default.DirectionsCar,
                    onToggle = { checklist[3] = !checklist[3] }
                )
                ChecklistRow(
                    checked = checklist[4],
                    title = stringResource(Res.string.date_safety_check_sos),
                    subtitle = stringResource(Res.string.date_safety_check_sos_desc),
                    icon = Icons.Default.Security,
                    onToggle = { checklist[4] = !checklist[4] }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Shared private composables
// ─────────────────────────────────────────────

@Composable
private fun ChecklistRow(
    checked: Boolean,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
        else MaterialTheme.colorScheme.surface
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (checked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (checked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.extended.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SafetyTipRow(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
