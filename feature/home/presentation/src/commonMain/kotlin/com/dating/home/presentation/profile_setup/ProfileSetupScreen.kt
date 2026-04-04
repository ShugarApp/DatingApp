package com.dating.home.presentation.profile_setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.profile_setup_basic_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_basic_title
import aura.feature.home.presentation.generated.resources.profile_setup_bio_counter
import aura.feature.home.presentation.generated.resources.profile_setup_bio_placeholder
import aura.feature.home.presentation.generated.resources.profile_setup_bio_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_bio_title
import aura.feature.home.presentation.generated.resources.profile_setup_birthdate
import aura.feature.home.presentation.generated.resources.profile_setup_birthdate_hint
import aura.feature.home.presentation.generated.resources.profile_setup_company_hint
import aura.feature.home.presentation.generated.resources.profile_setup_continue
import aura.feature.home.presentation.generated.resources.profile_setup_drinking
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_never
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_regularly
import aura.feature.home.presentation.generated.resources.profile_setup_drinking_socially
import aura.feature.home.presentation.generated.resources.profile_setup_education_hint
import aura.feature.home.presentation.generated.resources.profile_setup_finish
import aura.feature.home.presentation.generated.resources.profile_setup_gender
import aura.feature.home.presentation.generated.resources.profile_setup_gender_female
import aura.feature.home.presentation.generated.resources.profile_setup_gender_male
import aura.feature.home.presentation.generated.resources.profile_setup_gender_other
import aura.feature.home.presentation.generated.resources.profile_setup_height
import aura.feature.home.presentation.generated.resources.profile_setup_height_add
import aura.feature.home.presentation.generated.resources.profile_setup_height_value
import aura.feature.home.presentation.generated.resources.profile_setup_interests_count
import aura.feature.home.presentation.generated.resources.profile_setup_interests_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_interests_title
import aura.feature.home.presentation.generated.resources.profile_setup_job_hint
import aura.feature.home.presentation.generated.resources.profile_setup_lifestyle_subtitle
import aura.feature.home.presentation.generated.resources.profile_setup_lifestyle_title
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
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.home.presentation.profile.edit_profile.EditProfileAction
import com.dating.home.presentation.profile.edit_profile.EditProfileViewModel
import kotlin.time.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private enum class ProfileSetupStep {
    BIO, INTERESTS, BASIC_INFO, WORK, LIFESTYLE;

    val index get() = ordinal
    val total get() = entries.size
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var currentStep by remember { mutableStateOf(ProfileSetupStep.BIO) }
    var stepForward by remember { mutableStateOf(true) }

    val maxBirthDateMillis = now().toEpochMilliseconds() - 18L * 365L * 24L * 3600L * 1000L
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = state.birthDate?.toDateMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= maxBirthDateMillis
        }
    )
    LaunchedEffect(state.birthDate) {
        state.birthDate?.toDateMillis()?.let { datePickerState.selectedDateMillis = it }
    }
    var showDatePicker by remember { mutableStateOf(false) }

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

    fun skipAll() {
        onComplete()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Progress bar + step counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                val isLast = currentStep == ProfileSetupStep.LIFESTYLE
                ChirpButton(
                    text = if (isLast) {
                        stringResource(Res.string.profile_setup_finish)
                    } else {
                        stringResource(Res.string.profile_setup_continue)
                    },
                    onClick = ::goNext,
                    style = AppButtonStyle.PRIMARY_PURPLE,
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
                    ProfileSetupStep.BASIC_INFO -> {
                        BasicInfoStep(
                            state = state,
                            viewModel = viewModel,
                            onOpenDatePicker = { showDatePicker = true }
                        )
                    }
                    ProfileSetupStep.WORK -> WorkStep(state, viewModel)
                    ProfileSetupStep.LIFESTYLE -> LifestyleStep(state, viewModel)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onAction(EditProfileAction.OnBirthDateChanged(millis.toIsoDate()))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
// Step 3: Basic info (gender + birthdate)
// ────────────────────────────────────────────────

@Composable
private fun BasicInfoStep(
    state: com.dating.home.presentation.profile.edit_profile.EditProfileState,
    viewModel: EditProfileViewModel,
    onOpenDatePicker: () -> Unit
) {
    StepHeader(
        title = stringResource(Res.string.profile_setup_basic_title),
        subtitle = stringResource(Res.string.profile_setup_basic_subtitle)
    )
    Spacer(modifier = Modifier.height(28.dp))

    // Gender
    SetupSectionLabel(stringResource(Res.string.profile_setup_gender))
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            "MALE" to stringResource(Res.string.profile_setup_gender_male),
            "FEMALE" to stringResource(Res.string.profile_setup_gender_female),
            "OTHER" to stringResource(Res.string.profile_setup_gender_other)
        ).forEach { (value, label) ->
            SetupChip(
                label = label,
                selected = state.gender == value,
                onClick = {
                    viewModel.onAction(
                        EditProfileAction.OnGenderChanged(
                            if (state.gender == value) null else value
                        )
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Birthdate
    SetupSectionLabel(stringResource(Res.string.profile_setup_birthdate))
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(
        onClick = onOpenDatePicker,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = state.birthDate?.toDisplayDate()
                ?: stringResource(Res.string.profile_setup_birthdate_hint)
        )
    }

    val birthDateError = state.birthDateError
    if (birthDateError != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = birthDateError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ────────────────────────────────────────────────
// Step 4: Work & education
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
// Step 5: Lifestyle
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
        OutlinedButton(
            onClick = { viewModel.onAction(EditProfileAction.OnHeightChanged(170)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(Res.string.profile_setup_height_add))
        }
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = height.toFloat(),
                onValueChange = { viewModel.onAction(EditProfileAction.OnHeightChanged(it.toInt())) },
                valueRange = 100f..250f,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.profile_setup_height_value, height),
                style = MaterialTheme.typography.bodyMedium,
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

// ────────────────────────────────────────────────
// Date helpers (mirrors EditProfileScreen)
// ────────────────────────────────────────────────

private fun String.toDateMillis(): Long? = try {
    val date = LocalDate.parse(this)
    date.atStartOfDayIn(kotlinx.datetime.TimeZone.UTC).toEpochMilliseconds()
} catch (_: Exception) { null }

private fun Long.toIsoDate(): String {
    val date = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}

private fun String.toDisplayDate(): String = try {
    val date = LocalDate.parse(this)
    "${date.dayOfMonth} ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)} ${date.year}"
} catch (_: Exception) { this }

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
