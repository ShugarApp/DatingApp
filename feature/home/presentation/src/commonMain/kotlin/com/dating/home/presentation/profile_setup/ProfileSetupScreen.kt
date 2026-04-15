package com.dating.home.presentation.profile_setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_adventure
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_beach
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_cinema
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_coffee
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_concert
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_cooking
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_dinner
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_museum
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_picnic
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_travel
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_everyone
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_men
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_women
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_casual
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_friends
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_hookup
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_long_term
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_open
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_short_term
import aura.feature.home.presentation.generated.resources.profile_setup_bio_counter
import aura.feature.home.presentation.generated.resources.profile_setup_bio_placeholder
import aura.feature.home.presentation.generated.resources.profile_setup_bio_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_bio_title
import aura.feature.home.presentation.generated.resources.profile_setup_company_hint
import aura.feature.home.presentation.generated.resources.profile_setup_continue
import aura.feature.home.presentation.generated.resources.profile_setup_drinking
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_never
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_regularly
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_socially
import aura.feature.home.presentation.generated.resources.profile_setup_education_hint
import aura.feature.home.presentation.generated.resources.profile_setup_finish
import aura.feature.home.presentation.generated.resources.profile_setup_height
import aura.feature.home.presentation.generated.resources.profile_setup_height_add
import aura.feature.home.presentation.generated.resources.profile_setup_height_value
import aura.feature.home.presentation.generated.resources.profile_setup_interests_count
import aura.feature.home.presentation.generated.resources.profile_setup_interests_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_interests_title
import aura.feature.home.presentation.generated.resources.profile_setup_job_hint
import aura.feature.home.presentation.generated.resources.profile_setup_lifestyle_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_lifestyle_title
import aura.feature.home.presentation.generated.resources.profile_setup_preferences_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_preferences_title
import aura.feature.home.presentation.generated.resources.profile_setup_skip
import aura.feature.home.presentation.generated.resources.profile_setup_smoking
import aura.feature.home.presentation.generated.resources.profile_setup_smoking_never
import aura.feature.home.presentation.generated.resources.profile_setup_smoking_regularly
import aura.feature.home.presentation.generated.resources.profile_setup_smoking_sometimes
import aura.feature.home.presentation.generated.resources.profile_setup_step_of
import aura.feature.home.presentation.generated.resources.profile_setup_work_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_work_title
import aura.feature.home.presentation.generated.resources.profile_setup_zodiac
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.ChirpBase200
import com.dating.core.designsystem.theme.ChirpBase700
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.home.presentation.profile.edit_profile.EditProfileAction
import com.dating.home.presentation.profile.edit_profile.EditProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private enum class ProfileSetupStep {
    BIO, INTERESTS, WORK, LIFESTYLE, PREFERENCES;

    val index get() = ordinal
    val total get() = entries.size
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupFastScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var currentStep by remember { mutableStateOf(ProfileSetupStep.BIO) }
    var stepForward by remember { mutableStateOf(true) }


    fun goNext() {
        stepForward = true
        val next = ProfileSetupStep.entries.getOrNull(currentStep.index + 1)
        if (next != null) {
            currentStep = next
        } else {
            viewModel.onAction(EditProfileAction.OnSaveProfile)
            onComplete()
        }
    }

    fun goBack() {
        stepForward = false
        val prev = ProfileSetupStep.entries.getOrNull(currentStep.index - 1)
        if (prev != null) currentStep = prev
    }

    fun skipAll() {
        onComplete()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            ) {
                // Progress bar + step counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep.index > 0) {
                        IconButton(onClick = ::goBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.extended.textSecondary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    Text(
                        text = stringResource(
                            Res.string.profile_setup_step_of,
                            currentStep.index + 1,
                            currentStep.total
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.extended.textSecondary
                    )
                    TextButton(onClick = ::skipAll) {
                        Text(
                            text = stringResource(Res.string.profile_setup_skip),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.extended.textSecondary
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { (currentStep.index + 1).toFloat() / currentStep.total },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isLast = currentStep == ProfileSetupStep.PREFERENCES
                ChirpButton(
                    text = if (isLast) {
                        stringResource(Res.string.profile_setup_finish)
                    } else {
                        stringResource(Res.string.profile_setup_continue)
                    },
                    onClick = ::goNext,
                    style = AppButtonStyle.PRIMARY,
                    isLoading = state.isSavingProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                val enter = if (stepForward) {
                    slideInHorizontally { it } + fadeIn()
                } else {
                    slideInHorizontally { -it } + fadeIn()
                }
                val exit = if (stepForward) {
                    slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideOutHorizontally { it } + fadeOut()
                }
                enter togetherWith exit
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { step ->
            Column(
                modifier = Modifier
                    .clearFocusOnTap()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                when (step) {
                    ProfileSetupStep.BIO -> BioStep(state, viewModel)
                    ProfileSetupStep.INTERESTS -> InterestsStep(state, viewModel)
                    ProfileSetupStep.WORK -> WorkStep(state, viewModel)
                    ProfileSetupStep.LIFESTYLE -> LifestyleStep(state, viewModel)
                    ProfileSetupStep.PREFERENCES -> PreferencesStep(state, viewModel)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

}

// ────────────────────────────────────────────────
// Step 1: Bio
// ────────────────────────────────────────────────

@Composable
private fun BioStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_bio_title),
        subtitle = stringResource(Res.string.profile_setup_bio_subtitle)
    )
    Spacer(modifier = Modifier.height(28.dp))

    ChirpTextField(
        state = state.bioTextState,
        placeholder = stringResource(Res.string.profile_setup_bio_placeholder),
        modifier = Modifier.fillMaxWidth(),
        singleLine = false,
        minLines = 6
    )

    val bioLength = state.bioTextState.text.length
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(Res.string.profile_setup_bio_counter, bioLength),
            style = MaterialTheme.typography.bodySmall,
            color = if (bioLength > 500) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.extended.textSecondary
            }
        )
    }

    val bioError = state.bioError
    if (bioError != null) {
        Text(
            text = bioError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// ────────────────────────────────────────────────
// Step 2: Interests
// ────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_interests_title),
        subtitle = stringResource(Res.string.profile_setup_interests_subtitle)
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(
            Res.string.profile_setup_interests_count,
            state.selectedInterests.size
        ),
        style = MaterialTheme.typography.bodySmall,
        color = if (state.selectedInterests.size == 10) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.extended.textSecondary
        },
        fontWeight = if (state.selectedInterests.size == 10) FontWeight.SemiBold else FontWeight.Normal
    )

    Spacer(modifier = Modifier.height(20.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        state.availableInterests.forEach { interest ->
            ChirpChip(
                text = interestDisplayName(interest),
                isSelected = state.selectedInterests.contains(interest),
                onClick = { viewModel.onAction(EditProfileAction.OnInterestToggled(interest)) }
            )
        }
    }

    val interestsError = state.interestsError
    if (interestsError != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = interestsError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ────────────────────────────────────────────────
// Step 3: Work & education
// ────────────────────────────────────────────────

@Composable
private fun WorkStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_work_title),
        subtitle = stringResource(Res.string.profile_setup_work_subtitle)
    )
    Spacer(modifier = Modifier.height(28.dp))

    ChirpTextField(
        state = state.jobTitleTextState,
        placeholder = stringResource(Res.string.profile_setup_job_hint),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    ChirpTextField(
        state = state.companyTextState,
        placeholder = stringResource(Res.string.profile_setup_company_hint),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    ChirpTextField(
        state = state.educationTextState,
        placeholder = stringResource(Res.string.profile_setup_education_hint),
        modifier = Modifier.fillMaxWidth()
    )
}

// ────────────────────────────────────────────────
// Step 4: Lifestyle
// ────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LifestyleStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_lifestyle_title),
        subtitle = stringResource(Res.string.profile_setup_lifestyle_subtitle)
    )
    Spacer(modifier = Modifier.height(28.dp))

    // Height
    SetupSectionLabel(stringResource(Res.string.profile_setup_height))
    Spacer(modifier = Modifier.height(12.dp))
    val height = state.height
    if (height == null) {
        ChirpButton(
            text = stringResource(Res.string.profile_setup_height_add),
            onClick = { viewModel.onAction(EditProfileAction.OnHeightChanged(170)) },
            style = AppButtonStyle.SECONDARY,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = height.toFloat(),
                onValueChange = { viewModel.onAction(EditProfileAction.OnHeightChanged(it.toInt())) },
                valueRange = 100f..250f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = if (isSystemInDarkTheme()) ChirpBase700 else ChirpBase200
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.profile_setup_height_value, height),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                modifier = Modifier.width(56.dp)
            )
            IconButton(onClick = { viewModel.onAction(EditProfileAction.OnHeightChanged(null)) }) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Zodiac
    SetupSectionLabel(stringResource(Res.string.profile_setup_zodiac))
    Spacer(modifier = Modifier.height(12.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "ARIES", "TAURUS", "GEMINI", "CANCER", "LEO", "VIRGO",
            "LIBRA", "SCORPIO", "SAGITTARIUS", "CAPRICORN", "AQUARIUS", "PISCES"
        ).forEach { zodiac ->
            SetupChip(
                label = zodiac.lowercase().replaceFirstChar { it.uppercase() },
                selected = state.zodiac == zodiac,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnZodiacChanged(
                            if (state.zodiac == zodiac) null else zodiac
                        )
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Smoking
    SetupSectionLabel(stringResource(Res.string.profile_setup_smoking))
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            "NEVER" to stringResource(Res.string.profile_setup_smoking_never),
            "SOMETIMES" to stringResource(Res.string.profile_setup_smoking_sometimes),
            "REGULARLY" to stringResource(Res.string.profile_setup_smoking_regularly)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.smoking == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnSmokingChanged(
                            if (state.smoking == value) null else value
                        )
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Drinking
    SetupSectionLabel(stringResource(Res.string.profile_setup_drinking))
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            "NEVER" to stringResource(Res.string.profile_setup_drinking_never),
            "SOCIALLY" to stringResource(Res.string.profile_setup_drinking_socially),
            "REGULARLY" to stringResource(Res.string.profile_setup_drinking_regularly)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.drinking == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnDrinkingChanged(
                            if (state.drinking == value) null else value
                        )
                    )
                }
            )
        }
    }
}

// ────────────────────────────────────────────────
// Step 5: Preferences
// ────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferencesStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_preferences_title),
        subtitle = stringResource(Res.string.profile_setup_preferences_subtitle)
    )
    Spacer(modifier = Modifier.height(28.dp))

    // Interested In
    SetupSectionLabel(stringResource(Res.string.edit_profile_interested_in))
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            "MALE" to stringResource(Res.string.edit_profile_interested_in_men),
            "FEMALE" to stringResource(Res.string.edit_profile_interested_in_women),
            "EVERYONE" to stringResource(Res.string.edit_profile_interested_in_everyone)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.interestedIn == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnInterestedInChanged(
                            if (state.interestedIn == value) null else value
                        )
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Looking For
    SetupSectionLabel(stringResource(Res.string.edit_profile_looking_for))
    Spacer(modifier = Modifier.height(12.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "Long term" to stringResource(Res.string.edit_profile_looking_for_long_term),
            "Short term" to stringResource(Res.string.edit_profile_looking_for_short_term),
            "Casual dates" to stringResource(Res.string.edit_profile_looking_for_casual),
            "Hookup" to stringResource(Res.string.edit_profile_looking_for_hookup),
            "Friends" to stringResource(Res.string.edit_profile_looking_for_friends),
            "Open to anything" to stringResource(Res.string.edit_profile_looking_for_open)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.lookingFor == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnLookingForChanged(
                            if (state.lookingFor == value) null else value
                        )
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Ideal Date
    SetupSectionLabel(stringResource(Res.string.edit_profile_ideal_date))
    Spacer(modifier = Modifier.height(12.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "DINNER" to stringResource(Res.string.edit_profile_ideal_date_dinner),
            "COFFEE" to stringResource(Res.string.edit_profile_ideal_date_coffee),
            "ADVENTURE" to stringResource(Res.string.edit_profile_ideal_date_adventure),
            "CINEMA" to stringResource(Res.string.edit_profile_ideal_date_cinema),
            "PICNIC" to stringResource(Res.string.edit_profile_ideal_date_picnic),
            "TRAVEL" to stringResource(Res.string.edit_profile_ideal_date_travel),
            "CONCERT" to stringResource(Res.string.edit_profile_ideal_date_concert),
            "MUSEUM" to stringResource(Res.string.edit_profile_ideal_date_museum),
            "BEACH" to stringResource(Res.string.edit_profile_ideal_date_beach),
            "COOKING" to stringResource(Res.string.edit_profile_ideal_date_cooking)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.idealDate == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnIdealDateChanged(
                            if (state.idealDate == value) null else value
                        )
                    )
                }
            )
        }
    }
}

// ────────────────────────────────────────────────
// Shared components
// ────────────────────────────────────────────────

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        ),
        color = MaterialTheme.colorScheme.extended.textPrimary
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.extended.textSecondary,
        lineHeight = 24.sp
    )
}

@Composable
private fun SetupSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.extended.textPrimary
    )
}

@Composable
private fun SetupChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            selectedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

private fun interestDisplayName(key: String): String = when (key) {
    "photography" -> "Photography"
    "hiking" -> "Hiking"
    "music" -> "Music"
    "cooking" -> "Cooking"
    "travel" -> "Travel"
    "fitness" -> "Fitness"
    "reading" -> "Reading"
    "gaming" -> "Gaming"
    "art" -> "Art"
    "dancing" -> "Dancing"
    "yoga" -> "Yoga"
    "movies" -> "Movies"
    "sports" -> "Sports"
    "technology" -> "Technology"
    "fashion" -> "Fashion"
    "food" -> "Food"
    "coffee" -> "Coffee"
    "pets" -> "Pets"
    "nature" -> "Nature"
    "volunteering" -> "Volunteering"
    else -> key.replaceFirstChar { it.uppercase() }
}
