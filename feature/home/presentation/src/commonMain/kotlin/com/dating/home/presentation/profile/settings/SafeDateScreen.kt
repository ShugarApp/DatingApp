package com.dating.home.presentation.profile.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.safe_date_hero_desc
import aura.feature.home.presentation.generated.resources.safe_date_how_it_works
import aura.feature.home.presentation.generated.resources.safe_date_safety_1_desc
import aura.feature.home.presentation.generated.resources.safe_date_safety_1_title
import aura.feature.home.presentation.generated.resources.safe_date_safety_2_desc
import aura.feature.home.presentation.generated.resources.safe_date_safety_2_title
import aura.feature.home.presentation.generated.resources.safe_date_safety_3_desc
import aura.feature.home.presentation.generated.resources.safe_date_safety_3_title
import aura.feature.home.presentation.generated.resources.safe_date_safety_section
import aura.feature.home.presentation.generated.resources.safe_date_step_1_desc
import aura.feature.home.presentation.generated.resources.safe_date_step_1_title
import aura.feature.home.presentation.generated.resources.safe_date_step_2_desc
import aura.feature.home.presentation.generated.resources.safe_date_step_2_title
import aura.feature.home.presentation.generated.resources.safe_date_step_3_desc
import aura.feature.home.presentation.generated.resources.safe_date_step_3_title
import aura.feature.home.presentation.generated.resources.safe_date_step_4_desc
import aura.feature.home.presentation.generated.resources.safe_date_step_4_title
import aura.feature.home.presentation.generated.resources.safe_date_subtitle
import aura.feature.home.presentation.generated.resources.safe_date_title
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource

@Composable
fun SafeDateScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.safe_date_title),
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

            // Hero banner
            val gradient = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.secondaryContainer
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(gradient)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(Res.string.safe_date_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textPrimary
                        )
                        Text(
                            text = stringResource(Res.string.safe_date_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.safe_date_hero_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // How it works steps
            AccessCardList(title = stringResource(Res.string.safe_date_how_it_works)) {
                SafeDateStepRow(
                    stepNumber = 1,
                    icon = Icons.Default.CalendarMonth,
                    title = stringResource(Res.string.safe_date_step_1_title),
                    desc = stringResource(Res.string.safe_date_step_1_desc)
                )
                SafeDateStepRow(
                    stepNumber = 2,
                    icon = Icons.Default.ThumbUp,
                    title = stringResource(Res.string.safe_date_step_2_title),
                    desc = stringResource(Res.string.safe_date_step_2_desc)
                )
                SafeDateStepRow(
                    stepNumber = 3,
                    icon = Icons.Default.NotificationsActive,
                    title = stringResource(Res.string.safe_date_step_3_title),
                    desc = stringResource(Res.string.safe_date_step_3_desc)
                )
                SafeDateStepRow(
                    stepNumber = 4,
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.safe_date_step_4_title),
                    desc = stringResource(Res.string.safe_date_step_4_desc)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety features included
            AccessCardList(title = stringResource(Res.string.safe_date_safety_section)) {
                SafeDateFeatureRow(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(Res.string.safe_date_safety_1_title),
                    desc = stringResource(Res.string.safe_date_safety_1_desc)
                )
                SafeDateFeatureRow(
                    icon = Icons.Default.Sos,
                    title = stringResource(Res.string.safe_date_safety_2_title),
                    desc = stringResource(Res.string.safe_date_safety_2_desc)
                )
                SafeDateFeatureRow(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(Res.string.safe_date_safety_3_title),
                    desc = stringResource(Res.string.safe_date_safety_3_desc)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SafeDateStepRow(
    stepNumber: Int,
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
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

@Composable
private fun SafeDateFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
