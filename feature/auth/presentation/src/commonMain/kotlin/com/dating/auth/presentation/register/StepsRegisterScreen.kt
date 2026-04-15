package com.dating.auth.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.about_you
import aura.feature.auth.presentation.generated.resources.birth_date
import aura.feature.auth.presentation.generated.resources.birth_date_placeholder
import aura.feature.auth.presentation.generated.resources.everyone
import aura.feature.auth.presentation.generated.resources.gender
import aura.feature.auth.presentation.generated.resources.gender_female
import aura.feature.auth.presentation.generated.resources.gender_male
import aura.feature.auth.presentation.generated.resources.gender_non_binary
import aura.feature.auth.presentation.generated.resources.ideal_date
import aura.feature.auth.presentation.generated.resources.ideal_date_adventure
import aura.feature.auth.presentation.generated.resources.ideal_date_beach
import aura.feature.auth.presentation.generated.resources.ideal_date_cinema
import aura.feature.auth.presentation.generated.resources.ideal_date_coffee
import aura.feature.auth.presentation.generated.resources.ideal_date_concert
import aura.feature.auth.presentation.generated.resources.ideal_date_cooking
import aura.feature.auth.presentation.generated.resources.ideal_date_dinner
import aura.feature.auth.presentation.generated.resources.ideal_date_museum
import aura.feature.auth.presentation.generated.resources.ideal_date_picnic
import aura.feature.auth.presentation.generated.resources.ideal_date_section
import aura.feature.auth.presentation.generated.resources.ideal_date_travel
import aura.feature.auth.presentation.generated.resources.interest_casual_dates
import aura.feature.auth.presentation.generated.resources.interest_friends
import aura.feature.auth.presentation.generated.resources.interest_hookup
import aura.feature.auth.presentation.generated.resources.interest_long_term
import aura.feature.auth.presentation.generated.resources.interest_open_to_anything
import aura.feature.auth.presentation.generated.resources.interest_short_term
import aura.feature.auth.presentation.generated.resources.interested_in
import aura.feature.auth.presentation.generated.resources.looking_for
import aura.feature.auth.presentation.generated.resources.name_appearance_disclaimer
import aura.feature.auth.presentation.generated.resources.next
import aura.feature.auth.presentation.generated.resources.enter_your_name
import aura.feature.auth.presentation.generated.resources.register
import aura.feature.auth.presentation.generated.resources.register_welcome_cta
import aura.feature.auth.presentation.generated.resources.register_welcome_subtitle
import aura.feature.auth.presentation.generated.resources.register_welcome_title
import aura.feature.auth.presentation.generated.resources.what_is_your_name
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.RegisterTopBar
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StepsRegisterRoot(
    viewModel: StepsRegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onGoogleRegisterSuccess: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is StepsRegisterEvent.Success -> onRegisterSuccess(event.email)
            StepsRegisterEvent.GoogleSuccess -> onGoogleRegisterSuccess()
            StepsRegisterEvent.OnBack -> onBackClick()
        }
    }

    StepsRegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StepsRegisterScreen(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AuthSnackbarScaffold(
        topBar = {
            if (state.currentStep != RegisterStep.Welcome) {
                val totalSteps = 5
                val currentStepIndex = when (state.currentStep) {
                    RegisterStep.BasicInfo -> 1
                    RegisterStep.BirthDate -> 2
                    RegisterStep.GenderInterest -> 3
                    RegisterStep.LookingFor -> 4
                    RegisterStep.IdealDate -> 5
                    RegisterStep.Welcome -> 5
                }
                RegisterTopBar(
                    totalSteps = totalSteps,
                    currentStepIndex = currentStepIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    onBack = { onAction(StepsRegisterAction.OnBackClick) }
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { step ->
                when (step) {
                    RegisterStep.BasicInfo -> BasicInfoStep(
                        state = state,
                        onAction = onAction
                    )
                    RegisterStep.BirthDate -> BirthDateStep(
                        state = state,
                        onAction = onAction
                    )
                    RegisterStep.GenderInterest -> GenderInterestStep(
                        state = state,
                        onAction = onAction
                    )
                    RegisterStep.LookingFor -> LookingForStep(
                        state = state,
                        onAction = onAction
                    )
                    RegisterStep.IdealDate -> IdealDateStep(
                        state = state,
                        onAction = onAction
                    )
                    RegisterStep.Welcome -> WelcomeStep(
                        username = state.usernameTextState.text.toString(),
                        onContinue = { onAction(StepsRegisterAction.OnContinueClick) }
                    )
                }
            }

            if (state.currentStep != RegisterStep.Welcome) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 32.dp)
                ) {
                    if (state.registrationError != null) {
                        Text(
                            text = state.registrationError.asString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )
                    }
                    if (state.currentStep == RegisterStep.IdealDate) {
                        ChirpButton(
                            text = stringResource(Res.string.register),
                            onClick = { onAction(StepsRegisterAction.OnRegisterClick) },
                            enabled = state.canRegister,
                            isLoading = state.isRegistering,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ChirpButton(
                            text = stringResource(Res.string.next),
                            onClick = { onAction(StepsRegisterAction.OnNextClick) },
                            enabled = state.canProceed,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ── Step Composables ──────────────────────────────────────────────────────────

@Composable
private fun BasicInfoStep(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StepTitle(stringResource(Res.string.what_is_your_name))
        ChirpTextField(
            state = state.usernameTextState,
            placeholder = stringResource(Res.string.enter_your_name),
            supportingText = state.usernameError?.asString()
                ?: stringResource(Res.string.name_appearance_disclaimer),
            isError = state.usernameError != null,
            focusRequester = focusRequester,
            onFocusChanged = { onAction(StepsRegisterAction.OnInputTextFocusGain) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BirthDateStep(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StepTitle(stringResource(Res.string.birth_date))
        ChirpTextField(
            state = state.birthDateTextState,
            placeholder = stringResource(Res.string.birth_date_placeholder),
            title = stringResource(Res.string.birth_date),
            inputTransformation = DateInputTransformation,
            keyboardType = KeyboardType.Number,
            supportingText = state.birthDateError?.asString(),
            isError = state.birthDateError != null,
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun GenderInterestStep(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StepTitle(stringResource(Res.string.about_you))

        EmojiSelectionGrid(
            title = stringResource(Res.string.gender),
            options = listOf(
                stringResource(Res.string.gender_male),
                stringResource(Res.string.gender_female),
                stringResource(Res.string.gender_non_binary)
            ),
            emojis = listOf("👨", "👩", "🧑"),
            values = listOf("MALE", "FEMALE", "OTHER"),
            selectedValue = state.selectedGender,
            onSelect = { onAction(StepsRegisterAction.OnGenderSelect(it)) },
            columns = 3
        )

        Spacer(modifier = Modifier.height(28.dp))

        EmojiSelectionGrid(
            title = stringResource(Res.string.interested_in),
            options = listOf(
                stringResource(Res.string.gender_male),
                stringResource(Res.string.gender_female),
                stringResource(Res.string.everyone)
            ),
            emojis = listOf("👨", "👩", "🌈"),
            values = listOf("MALE", "FEMALE", "EVERYONE"),
            selectedValue = state.selectedInterest,
            onSelect = { onAction(StepsRegisterAction.OnInterestSelect(it)) },
            columns = 3
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LookingForStep(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StepTitle(stringResource(Res.string.looking_for))

        EmojiSelectionGrid(
            title = stringResource(Res.string.looking_for),
            options = listOf(
                stringResource(Res.string.interest_long_term),
                stringResource(Res.string.interest_short_term),
                stringResource(Res.string.interest_casual_dates),
                stringResource(Res.string.interest_hookup),
                stringResource(Res.string.interest_friends),
                stringResource(Res.string.interest_open_to_anything)
            ),
            emojis = listOf("💍", "⚡", "🌙", "🔥", "🤝", "✨"),
            values = listOf("Long term", "Short term", "Casual dates", "Hookup", "Friends", "Open to anything"),
            selectedValue = state.selectedLookingFor,
            onSelect = { onAction(StepsRegisterAction.OnLookingForSelect(it)) },
            columns = 2
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun IdealDateStep(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StepTitle(stringResource(Res.string.ideal_date))

        EmojiSelectionGrid(
            title = stringResource(Res.string.ideal_date_section),
            options = listOf(
                stringResource(Res.string.ideal_date_dinner),
                stringResource(Res.string.ideal_date_coffee),
                stringResource(Res.string.ideal_date_adventure),
                stringResource(Res.string.ideal_date_cinema),
                stringResource(Res.string.ideal_date_picnic),
                stringResource(Res.string.ideal_date_travel),
                stringResource(Res.string.ideal_date_concert),
                stringResource(Res.string.ideal_date_museum),
                stringResource(Res.string.ideal_date_beach),
                stringResource(Res.string.ideal_date_cooking)
            ),
            emojis = listOf("🍽️", "☕", "🏔️", "🎬", "🧺", "✈️", "🎵", "🏛️", "🏖️", "👨‍🍳"),
            values = listOf(
                "DINNER", "COFFEE", "ADVENTURE", "CINEMA",
                "PICNIC", "TRAVEL", "CONCERT", "MUSEUM",
                "BEACH", "COOKING"
            ),
            selectedValue = state.selectedIdealDate,
            onSelect = { onAction(StepsRegisterAction.OnIdealDateSelect(it)) },
            columns = 2
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WelcomeStep(
    username: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.register_welcome_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (username.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.register_welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        ChirpButton(
            text = stringResource(Res.string.register_welcome_cta),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Shared Components ─────────────────────────────────────────────────────────

@Composable
fun StepTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    )
}

@Composable
private fun EmojiSelectionGrid(
    title: String,
    options: List<String>,
    emojis: List<String>,
    values: List<String>,
    selectedValue: String?,
    onSelect: (String) -> Unit,
    columns: Int = 2,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        val rows = options.chunked(columns)
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEachIndexed { colIndex, label ->
                    val globalIndex = rowIndex * columns + colIndex
                    val value = values.getOrNull(globalIndex) ?: label
                    val emoji = emojis.getOrNull(globalIndex) ?: ""
                    EmojiSelectCard(
                        emoji = emoji,
                        label = label,
                        isSelected = value == selectedValue,
                        onClick = { onSelect(value) },
                        cardHeight = if (columns == 3) 88 else 108,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining empty slots in last row
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EmojiSelectCard(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    cardHeight: Int = 108,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 160)
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(durationMillis = 160)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 160)
    )

    Box(
        modifier = modifier
            .height(cardHeight.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSteps() {
    AppTheme {
        StepsRegisterScreen(
            state = StepsRegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
