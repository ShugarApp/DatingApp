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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.feed_filter_verified_only
import aura.feature.home.presentation.generated.resources.feed_filter_verified_only_desc
import aura.feature.home.presentation.generated.resources.network_error
import aura.feature.home.presentation.generated.resources.save
import aura.feature.home.presentation.generated.resources.settings_100_km
import aura.feature.home.presentation.generated.resources.settings_age_range
import aura.feature.home.presentation.generated.resources.settings_discovery
import aura.feature.home.presentation.generated.resources.settings_max_distance
import aura.feature.home.presentation.generated.resources.settings_show_me
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.discovery.Gender
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverySettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.settings_discovery),
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
            Spacer(modifier = Modifier.height(16.dp))

            AccessCardList(
                title = stringResource(Res.string.settings_discovery)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Tune,
                    title = stringResource(Res.string.settings_max_distance),
                    subtitle = state.maxDistance?.let { "${it.roundToInt()} km" }
                        ?: stringResource(Res.string.settings_100_km),
                    onClick = { viewModel.onAction(SettingsAction.OnMaxDistanceClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Groups,
                    title = stringResource(Res.string.settings_show_me),
                    subtitle = when (state.showMe) {
                        Gender.MEN -> "Men"
                        Gender.WOMEN -> "Women"
                        Gender.EVERYONE -> "Everyone"
                    },
                    onClick = { viewModel.onAction(SettingsAction.OnShowMeClick) }
                )
                AccessCardItem(
                    icon = Icons.Default.Cake,
                    title = stringResource(Res.string.settings_age_range),
                    subtitle = "${state.minAge} - ${state.maxAge}",
                    onClick = { viewModel.onAction(SettingsAction.OnAgeRangeClick) }
                )

                // Verified Profiles Only switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.feed_filter_verified_only),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(Res.string.feed_filter_verified_only_desc),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textDisabled
                        )
                    }
                    Switch(
                        checked = state.verifiedProfilesOnly,
                        onCheckedChange = { enabled ->
                            viewModel.onAction(SettingsAction.OnVerifiedProfilesOnlyChanged(enabled))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Max Distance dialog
        if (state.showDistanceDialog) {
            DiscoveryMaxDistanceDialog(
                currentDistance = state.maxDistance ?: 100.0,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { distance ->
                    viewModel.onAction(SettingsAction.OnMaxDistanceChanged(distance))
                }
            )
        }

        // Show Me dialog
        if (state.showGenderDialog) {
            DiscoveryShowMeDialog(
                currentGender = state.showMe,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { gender ->
                    viewModel.onAction(SettingsAction.OnShowMeChanged(gender))
                }
            )
        }

        // Age Range dialog
        if (state.showAgeRangeDialog) {
            DiscoveryAgeRangeDialog(
                currentMin = state.minAge,
                currentMax = state.maxAge,
                onDismiss = { viewModel.onAction(SettingsAction.OnDismissDiscoveryDialog) },
                onConfirm = { min, max ->
                    viewModel.onAction(SettingsAction.OnAgeRangeChanged(min, max))
                }
            )
        }

        // Error dialog
        state.errorMessage?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = { viewModel.onAction(SettingsAction.OnDismissError) },
                title = { Text(stringResource(Res.string.network_error)) },
                text = { Text(errorMessage.asString()) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(SettingsAction.OnDismissError) }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
internal fun DiscoveryMaxDistanceDialog(
    currentDistance: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double?) -> Unit
) {
    var distance by remember { mutableFloatStateOf(currentDistance.toFloat()) }

    DiscoveryDialog(
        title = stringResource(Res.string.settings_max_distance),
        icon = Icons.Default.Tune,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(if (distance >= 500f) null else distance.toDouble())
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (distance >= 500f) "No limit" else "${distance.roundToInt()} km",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = distance,
            onValueChange = { distance = it },
            valueRange = 5f..500f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "5 km",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
            Text(
                text = "No limit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}

@Composable
internal fun DiscoveryShowMeDialog(
    currentGender: Gender,
    onDismiss: () -> Unit,
    onConfirm: (Gender) -> Unit
) {
    var selected by remember { mutableStateOf(currentGender) }

    DiscoveryDialog(
        title = stringResource(Res.string.settings_show_me),
        icon = Icons.Default.Groups,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Gender.entries.forEach { gender ->
                val isSelected = selected == gender
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { selected = gender }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = when (gender) {
                            Gender.MEN -> "Men"
                            Gender.WOMEN -> "Women"
                            Gender.EVERYONE -> "Everyone"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.extended.textPrimary
                        },
                        modifier = Modifier.weight(1f)
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoveryAgeRangeDialog(
    currentMin: Int,
    currentMax: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var range by remember {
        mutableStateOf(currentMin.toFloat()..currentMax.toFloat())
    }

    DiscoveryDialog(
        title = stringResource(Res.string.settings_age_range),
        icon = Icons.Default.Cake,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(range.start.roundToInt(), range.endInclusive.roundToInt())
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            AgeBox(label = "Min", value = "${range.start.roundToInt()}")
            AgeBox(label = "Max", value = "${range.endInclusive.roundToInt()}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        RangeSlider(
            value = range,
            onValueChange = { range = it },
            valueRange = 18f..80f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "18",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
            Text(
                text = "80",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}

@Composable
private fun AgeBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
internal fun DiscoveryDialog(
    title: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .widthIn(max = 480.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.extended.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChirpButton(
                        text = stringResource(Res.string.cancel),
                        onClick = onDismiss,
                        style = AppButtonStyle.SECONDARY
                    )
                    ChirpButton(
                        text = stringResource(Res.string.save),
                        onClick = onConfirm,
                        style = AppButtonStyle.PRIMARY
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}
