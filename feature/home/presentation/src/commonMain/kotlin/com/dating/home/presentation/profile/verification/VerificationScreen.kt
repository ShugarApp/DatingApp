package com.dating.home.presentation.profile.verification

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.verification_button
import aura.feature.home.presentation.generated.resources.verification_desc
import aura.feature.home.presentation.generated.resources.verification_feature_1_desc
import aura.feature.home.presentation.generated.resources.verification_feature_1_title
import aura.feature.home.presentation.generated.resources.verification_feature_2_desc
import aura.feature.home.presentation.generated.resources.verification_feature_2_title
import aura.feature.home.presentation.generated.resources.verification_feature_3_desc
import aura.feature.home.presentation.generated.resources.verification_feature_3_title
import aura.feature.home.presentation.generated.resources.verification_footer
import aura.feature.home.presentation.generated.resources.verification_subtitle
import aura.feature.home.presentation.generated.resources.verification_title
import com.dating.core.domain.auth.VerificationStatus
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import com.dating.core.presentation.permissions.Permission
import com.dating.core.presentation.permissions.PermissionState
import com.dating.core.presentation.permissions.rememberPermissionController
import com.dating.home.presentation.components.VerifiedBlue
import com.dating.home.presentation.profile.mediapicker.rememberSelfieCameraLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VerificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                VerificationEvent.NavigateBack -> onBack()
            }
        }
    }

    val cameraLauncher = rememberSelfieCameraLauncher { pickedData ->
        if (pickedData != null) {
            viewModel.onAction(VerificationAction.OnSelfieCaptured(pickedData))
        }
    }

    // Wraps the camera launch with a runtime CAMERA permission request
    val launchCameraWithPermission: () -> Unit = {
        scope.launch {
            val permState = permissionController.requestPermission(Permission.CAMERA)
            if (permState == PermissionState.GRANTED) {
                cameraLauncher.launch()
            } else {
                viewModel.onAction(
                    VerificationAction.OnPermissionDenied(
                        permanently = permState == PermissionState.PERMANENTLY_DENIED
                    )
                )
            }
        }
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(VerificationAction.OnDismissError) },
            title = { Text("Error") },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(VerificationAction.OnDismissError) }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.verification_title),
                onBack = { viewModel.onAction(VerificationAction.OnBack) }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.step,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) { step ->
            when (step) {
                VerificationStep.STATUS -> StatusStepContent(
                    state = state,
                    onStartCapture = { viewModel.onAction(VerificationAction.OnStartCapture) },
                    onRetry = { viewModel.onAction(VerificationAction.OnRetry) }
                )
                VerificationStep.GUIDANCE -> GuidanceStepContent(
                    onLaunchCamera = launchCameraWithPermission
                )
                VerificationStep.PROCESSING -> ProcessingStepContent()
                VerificationStep.RESULT -> ResultStepContent(
                    state = state,
                    onRetry = { viewModel.onAction(VerificationAction.OnRetry) },
                    onDone = { viewModel.onAction(VerificationAction.OnBack) }
                )
            }
        }
    }
}

// ─── Step 1: Status ────────────────────────���──────────────────────────────────

@Composable
private fun StatusStepContent(
    state: VerificationState,
    onStartCapture: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // Hero icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.verification_subtitle),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.extended.textPrimary
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.verification_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Current status banner (only when not UNVERIFIED)
        when (state.verificationStatus) {
            VerificationStatus.VERIFIED -> {
                VerificationStatusBanner(
                    icon = Icons.Default.CheckCircle,
                    text = "Your profile is verified",
                    tint = Color(0xFF4CAF50),
                    background = Color(0xFF4CAF50).copy(alpha = 0.12f)
                )
                Spacer(Modifier.height(16.dp))
            }
            VerificationStatus.PENDING -> {
                VerificationStatusBanner(
                    icon = Icons.Default.HourglassEmpty,
                    text = "Verification in progress...",
                    tint = Color(0xFFFFA000),
                    background = Color(0xFFFFA000).copy(alpha = 0.12f)
                )
                Spacer(Modifier.height(16.dp))
            }
            VerificationStatus.REJECTED -> {
                VerificationStatusBanner(
                    icon = Icons.Default.Error,
                    text = state.verification?.rejectionReason ?: "Verification was rejected",
                    tint = MaterialTheme.colorScheme.error,
                    background = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                )
                Spacer(Modifier.height(16.dp))
            }
            VerificationStatus.UNVERIFIED -> Unit
        }

        // Feature list
        SecurityFeatureItem(
            icon = Icons.Default.Lock,
            title = stringResource(Res.string.verification_feature_1_title),
            description = stringResource(Res.string.verification_feature_1_desc)
        )
        Spacer(Modifier.height(16.dp))
        SecurityFeatureItem(
            icon = Icons.Default.VerifiedUser,
            title = stringResource(Res.string.verification_feature_2_title),
            description = stringResource(Res.string.verification_feature_2_desc)
        )
        Spacer(Modifier.height(16.dp))
        SecurityFeatureItem(
            icon = Icons.Default.Security,
            title = stringResource(Res.string.verification_feature_3_title),
            description = stringResource(Res.string.verification_feature_3_desc)
        )

        Spacer(Modifier.height(32.dp))

        when (state.verificationStatus) {
            VerificationStatus.UNVERIFIED -> {
                ChirpButton(
                    text = stringResource(Res.string.verification_button),
                    onClick = onStartCapture,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VerificationStatus.REJECTED -> {
                ChirpButton(
                    text = "Retry Verification",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VerificationStatus.VERIFIED, VerificationStatus.PENDING -> Unit
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.verification_footer),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.extended.textDisabled
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ─── Step 2: Guidance ─────────────────────────────────────────────────────────

@Composable
private fun GuidanceStepContent(onLaunchCamera: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Position your face",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.extended.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Align your face within the oval guide, ensure good lighting and look straight at the camera.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // Oval face guide preview
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(0.75f)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                .drawBehind {
                    val strokeWidth = 4.dp.toPx()
                    val inset = strokeWidth / 2
                    drawOval(
                        color = primaryColor,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        GuidanceTip(text = "Use the front-facing camera")
        Spacer(Modifier.height(8.dp))
        GuidanceTip(text = "Good lighting, no glasses or hat")
        Spacer(Modifier.height(8.dp))
        GuidanceTip(text = "Look directly at the camera")

        Spacer(Modifier.height(40.dp))

        ChirpButton(
            text = "Take Selfie",
            onClick = onLaunchCamera,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GuidanceTip(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
    }
}

// ─── Step 3: Processing ─────────────────────────────────���─────────────────────

@Composable
private fun ProcessingStepContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "processing_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(72.dp),
                strokeWidth = 5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Verifying your identity…",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "This usually takes just a few seconds.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

// ─── Step 4: Result ───────────────────────────────────────────────────────────

@Composable
private fun ResultStepContent(
    state: VerificationState,
    onRetry: () -> Unit,
    onDone: () -> Unit
) {
    val isVerified = state.verificationStatus == VerificationStatus.VERIFIED

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isVerified) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = VerifiedBlue,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Profile Verified!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.extended.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Your profile now displays a verified badge. Other users can trust you are who you say you are.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))
                ChirpButton(
                    text = "Done",
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Verification Failed",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.extended.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.verification?.rejectionReason
                        ?: "We could not verify your identity. Please try again with a clear, well-lit selfie.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))
                ChirpButton(
                    text = "Try Again",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                ChirpButton(
                    text = "Cancel",
                    onClick = onDone,
                    style = AppButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ─── Shared Components ────────────────────────────────────────────────────────

@Composable
private fun VerificationStatusBanner(
    icon: ImageVector,
    text: String,
    tint: Color,
    background: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = tint
        )
    }
}

@Composable
private fun SecurityFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}
