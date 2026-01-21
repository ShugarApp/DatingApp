package com.dating.auth.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.about_you
import aura.feature.auth.presentation.generated.resources.birth_date
import aura.feature.auth.presentation.generated.resources.birth_date_placeholder
import aura.feature.auth.presentation.generated.resources.gender
import aura.feature.auth.presentation.generated.resources.gender_female
import aura.feature.auth.presentation.generated.resources.gender_male
import aura.feature.auth.presentation.generated.resources.gender_non_binary
import aura.feature.auth.presentation.generated.resources.interest_casual
import aura.feature.auth.presentation.generated.resources.interest_friends
import aura.feature.auth.presentation.generated.resources.interest_relationship
import aura.feature.auth.presentation.generated.resources.interested_in
import aura.feature.auth.presentation.generated.resources.looking_for
import aura.feature.auth.presentation.generated.resources.next
import aura.feature.auth.presentation.generated.resources.register
import aura.feature.auth.presentation.generated.resources.what_is_your_name
import aura.feature.auth.presentation.generated.resources.enter_your_name
import aura.feature.auth.presentation.generated.resources.name_appearance_disclaimer
import aura.feature.auth.presentation.generated.resources.everyone
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.chips.ChirpChip
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
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is StepsRegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }

            StepsRegisterEvent.OnBack -> {
                onBackClick()
            }
        }
    }

    StepsRegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun StepsRegisterScreen(
    state: StepsRegisterState,
    onAction: (StepsRegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AuthSnackbarScaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val totalSteps = 4
                val currentStepIndex = when (state.currentStep) {
                    RegisterStep.BasicInfo -> 1
                    RegisterStep.BirthDate -> 2
                    RegisterStep.GenderInterest -> 3
                    RegisterStep.LookingFor -> 4
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
            // ──────────────── CONTENT ────────────────
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    if (state.registrationError != null) {
                        Text(
                            text = state.registrationError.asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    when (step) {
                        RegisterStep.BasicInfo -> {
                            StepTitle(stringResource(Res.string.what_is_your_name))
                            ChirpTextField(
                                state = state.usernameTextState,
                                placeholder = stringResource(Res.string.enter_your_name),
                                supportingText = state.usernameError?.asString()
                                    ?: stringResource(Res.string.name_appearance_disclaimer),
                                isError = state.usernameError != null,
                                onFocusChanged = {
                                    onAction(StepsRegisterAction.OnInputTextFocusGain)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        RegisterStep.BirthDate -> {
                            StepTitle(stringResource(Res.string.birth_date))

                            ChirpTextField(
                                state = state.birthDateTextState,
                                placeholder = stringResource(Res.string.birth_date_placeholder),
                                title = stringResource(Res.string.birth_date),
                                inputTransformation = DateInputTransformation,
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        RegisterStep.GenderInterest -> {
                            StepTitle(stringResource(Res.string.about_you))

                            SectionSelection(
                                title = stringResource(Res.string.gender),
                                options = listOf(
                                    stringResource(Res.string.gender_male),
                                    stringResource(Res.string.gender_female),
                                    stringResource(Res.string.gender_non_binary)
                                ),
                                selectedOption = state.selectedGender,
                                onOptionSelected = {
                                    onAction(StepsRegisterAction.OnGenderSelect(it))
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            SectionSelection(
                                title = stringResource(Res.string.interested_in),
                                options = listOf(
                                    stringResource(Res.string.gender_male),
                                    stringResource(Res.string.gender_female),
                                    stringResource(Res.string.everyone)
                                ),
                                selectedOption = state.selectedInterest,
                                onOptionSelected = {
                                    onAction(StepsRegisterAction.OnInterestSelect(it))
                                }
                            )
                        }

                        RegisterStep.LookingFor -> {
                            StepTitle(stringResource(Res.string.looking_for))
                            SectionSelection(
                                title = stringResource(Res.string.looking_for),
                                options = listOf(
                                    stringResource(Res.string.interest_relationship),
                                    stringResource(Res.string.interest_casual),
                                    stringResource(Res.string.interest_friends)
                                ),
                                selectedOption = state.selectedLookingFor,
                                onOptionSelected = {
                                    onAction(StepsRegisterAction.OnLookingForSelect(it))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ──────────────── BOTTOM ACTIONS ────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 64.dp)
            ) {
                if (state.currentStep == RegisterStep.LookingFor) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SectionSelection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                ChirpChip(
                    text = option,
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun StepTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    )
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
