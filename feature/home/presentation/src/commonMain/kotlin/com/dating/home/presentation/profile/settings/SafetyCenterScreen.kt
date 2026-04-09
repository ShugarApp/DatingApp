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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.date_safety_checklist_section
import aura.feature.home.presentation.generated.resources.date_safety_tips_section
import aura.feature.home.presentation.generated.resources.date_safety_tips_subtitle
import aura.feature.home.presentation.generated.resources.safe_date_subtitle
import aura.feature.home.presentation.generated.resources.safe_date_title
import aura.feature.home.presentation.generated.resources.emergency_contacts_title
import aura.feature.home.presentation.generated.resources.emergency_feature_description
import aura.feature.home.presentation.generated.resources.emergency_how_it_works
import aura.feature.home.presentation.generated.resources.emergency_safety_section
import aura.feature.home.presentation.generated.resources.profile_safety_center
import aura.feature.home.presentation.generated.resources.safety_center_contacts_hint
import aura.feature.home.presentation.generated.resources.safety_center_hero_desc
import aura.feature.home.presentation.generated.resources.safety_center_manage_contacts
import aura.feature.home.presentation.generated.resources.settings_date_safety_tips
import com.dating.core.designsystem.components.brand.ChirpHorizontalDivider
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SafetyCenterScreen(
    onBack: () -> Unit,
    onEmergencyContacts: () -> Unit = {},
    onEmergencyTutorial: () -> Unit = {},
    onDateSafetyTips: () -> Unit = {},
    onDateSafetyChecklist: () -> Unit = {},
    onSafeDate: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SettingsEvent.OnNavigateToEmergencyContacts -> onEmergencyContacts()
            SettingsEvent.OnNavigateToEmergencyTutorial -> onEmergencyTutorial()
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.profile_safety_center),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SafetyHeroBanner(
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Emergency Contact section
            AccessCardList(title = stringResource(Res.string.emergency_contacts_title)) {
                EmergencyFeatureToggleRow(
                    isEnabled = state.isEmergencyEnabled,
                    onToggle = { viewModel.onAction(SettingsAction.OnEmergencyToggle(it)) },
                    onHowItWorks = { viewModel.onAction(SettingsAction.OnEmergencyTutorialClick) }
                )

                if (state.isEmergencyEnabled) {
                    ChirpHorizontalDivider()
                    AccessCardItem(
                        icon = Icons.Default.ContactPhone,
                        title = stringResource(Res.string.safety_center_manage_contacts),
                        subtitle = stringResource(Res.string.safety_center_contacts_hint),
                        iconBgColor = MaterialTheme.colorScheme.errorContainer,
                        onClick = { viewModel.onAction(SettingsAction.OnEmergencyContactsClick) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tips section
            AccessCardList(title = stringResource(Res.string.settings_date_safety_tips)) {
                AccessCardItem(
                    icon = Icons.Default.CalendarMonth,
                    title = stringResource(Res.string.safe_date_title),
                    subtitle = stringResource(Res.string.safe_date_subtitle),
                    onClick = onSafeDate
                )
                ChirpHorizontalDivider()
                AccessCardItem(
                    icon = Icons.Default.Security,
                    title = stringResource(Res.string.date_safety_tips_section),
                    subtitle = stringResource(Res.string.date_safety_tips_subtitle),
                    onClick = onDateSafetyTips
                )
                ChirpHorizontalDivider()
                AccessCardItem(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(Res.string.date_safety_checklist_section),
                    onClick = onDateSafetyChecklist
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SafetyHeroBanner(modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.primaryContainer
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
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
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.profile_safety_center),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Text(
                    text = stringResource(Res.string.safety_center_hero_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmergencyFeatureToggleRow(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onHowItWorks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ContactPhone,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.emergency_contacts_title),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.emergency_feature_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onHowItWorks) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = stringResource(Res.string.emergency_how_it_works),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}
