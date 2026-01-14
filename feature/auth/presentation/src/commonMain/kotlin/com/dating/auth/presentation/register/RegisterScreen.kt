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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.auth.presentation.generated.resources.Res
import aura.feature.auth.presentation.generated.resources.about_you
import aura.feature.auth.presentation.generated.resources.basic_info
import aura.feature.auth.presentation.generated.resources.birth_date
import aura.feature.auth.presentation.generated.resources.birth_date_placeholder
import aura.feature.auth.presentation.generated.resources.create_account
import aura.feature.auth.presentation.generated.resources.email
import aura.feature.auth.presentation.generated.resources.email_placeholder
import aura.feature.auth.presentation.generated.resources.gender
import aura.feature.auth.presentation.generated.resources.gender_female
import aura.feature.auth.presentation.generated.resources.gender_male
import aura.feature.auth.presentation.generated.resources.gender_non_binary
import aura.feature.auth.presentation.generated.resources.interest_casual
import aura.feature.auth.presentation.generated.resources.interest_friends
import aura.feature.auth.presentation.generated.resources.interest_relationship
import aura.feature.auth.presentation.generated.resources.interested_in
import aura.feature.auth.presentation.generated.resources.login
import aura.feature.auth.presentation.generated.resources.looking_for
import aura.feature.auth.presentation.generated.resources.next
import aura.feature.auth.presentation.generated.resources.password
import aura.feature.auth.presentation.generated.resources.password_hint
import aura.feature.auth.presentation.generated.resources.register
import aura.feature.auth.presentation.generated.resources.username
import aura.feature.auth.presentation.generated.resources.username_hint
import aura.feature.auth.presentation.generated.resources.username_placeholder
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.layouts.AuthSnackbarScaffold
import com.dating.core.designsystem.components.textfields.ChirpPasswordTextField
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }

            RegisterEvent.OnBack -> {
                onBackClick()
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AuthSnackbarScaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppCenterTopBar(
                    title = "",
                    containerColor = MaterialTheme.colorScheme.background,
                    onBack = { onAction(RegisterAction.OnBackClick) }
                )
                // PROGRESS SECTION (Hide on Credentials step)
                if (state.currentStep != RegisterStep.Credentials) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val totalSteps = 5
                    val currentStepIndex = when (state.currentStep) {
                        RegisterStep.Credentials -> 1
                        RegisterStep.BasicInfo -> 2
                        RegisterStep.BirthDate -> 3
                        RegisterStep.GenderInterest -> 4
                        RegisterStep.LookingFor -> 5
                    }
                    val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = currentStepIndex / totalSteps.toFloat(),
                        label = "ProgressAnimation"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }
        },
        snackbarHostState = snackbarHostState
    ) {
        /***/
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    if (state.currentStep == RegisterStep.Credentials) {
                        AppBrandLogo(modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(32.dp))
                    }

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

                        RegisterStep.Credentials -> {
                            Text(
                                text = stringResource(Res.string.create_account),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            ChirpTextField(
                                state = state.emailTextState,
                                placeholder = stringResource(Res.string.email_placeholder),
                                title = stringResource(Res.string.email),
                                supportingText = state.emailError?.asString(),
                                isError = state.emailError != null,
                                onFocusChanged = {
                                    onAction(RegisterAction.OnInputTextFocusGain)
                                },
                                keyboardType = KeyboardType.Email,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ChirpPasswordTextField(
                                state = state.passwordTextState,
                                placeholder = stringResource(Res.string.password),
                                title = stringResource(Res.string.password),
                                supportingText = state.passwordError?.asString() ?: stringResource(Res.string.password_hint),
                                isError = state.passwordError != null,
                                onFocusChanged = {
                                    onAction(RegisterAction.OnInputTextFocusGain)
                                },
                                onToggleVisibilityClick = {
                                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                                },
                                isPasswordVisible = state.isPasswordVisible,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        RegisterStep.BasicInfo -> {
                            StepTitle(stringResource(Res.string.basic_info))

                            ChirpTextField(
                                state = state.usernameTextState,
                                placeholder = stringResource(Res.string.username_placeholder),
                                title = stringResource(Res.string.username),
                                supportingText = state.usernameError?.asString()
                                    ?: stringResource(Res.string.username_hint),
                                isError = state.usernameError != null,
                                onFocusChanged = {
                                    onAction(RegisterAction.OnInputTextFocusGain)
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
                                    onAction(RegisterAction.OnGenderSelect(it))
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            SectionSelection(
                                title = stringResource(Res.string.interested_in),
                                options = listOf(
                                    stringResource(Res.string.gender_male),
                                    stringResource(Res.string.gender_female),
                                    "Everyone"
                                ),
                                selectedOption = state.selectedInterest,
                                onOptionSelected = {
                                    onAction(RegisterAction.OnInterestSelect(it))
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
                                    onAction(RegisterAction.OnLookingForSelect(it))
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
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                if (state.currentStep == RegisterStep.LookingFor) {
                    ChirpButton(
                        text = stringResource(Res.string.register),
                        onClick = { onAction(RegisterAction.OnRegisterClick) },
                        enabled = state.canRegister,
                        isLoading = state.isRegistering,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ChirpButton(
                        text = stringResource(Res.string.next),
                        onClick = { onAction(RegisterAction.OnNextClick) },
                        enabled = state.canProceed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (state.currentStep == RegisterStep.Credentials) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ChirpButton(
                        text = stringResource(Res.string.login),
                        onClick = { onAction(RegisterAction.OnLoginClick) },
                        style = AppButtonStyle.TEXT,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        /***/

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
private fun Preview() {
    AppTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}