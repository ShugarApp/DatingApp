package com.dating.home.presentation.photo_onboarding

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import com.dating.core.designsystem.components.buttons.ChirpButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.photo_onboarding_continue
import aura.feature.home.presentation.generated.resources.photo_onboarding_photos_count
import aura.feature.home.presentation.generated.resources.photo_onboarding_subtitle
import aura.feature.home.presentation.generated.resources.photo_onboarding_title
import aura.feature.home.presentation.generated.resources.photo_tips_button
import aura.feature.home.presentation.generated.resources.photo_tips_got_it
import aura.feature.home.presentation.generated.resources.photo_tips_subtitle
import aura.feature.home.presentation.generated.resources.photo_tips_tip1_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip1_title
import aura.feature.home.presentation.generated.resources.photo_tips_tip2_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip2_title
import aura.feature.home.presentation.generated.resources.photo_tips_tip3_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip3_title
import aura.feature.home.presentation.generated.resources.photo_tips_tip4_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip4_title
import aura.feature.home.presentation.generated.resources.photo_tips_tip5_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip5_title
import aura.feature.home.presentation.generated.resources.photo_tips_tip6_desc
import aura.feature.home.presentation.generated.resources.photo_tips_tip6_title
import aura.feature.home.presentation.generated.resources.photo_tips_title
import com.dating.home.domain.upload.PhotoUploadManager
import com.dating.home.domain.upload.PhotoUploadRequest
import com.dating.home.presentation.profile.edit_profile.PhotoGrid
import com.dating.home.presentation.profile.mediapicker.rememberMultiImagePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoOnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadManager: PhotoUploadManager = koinInject()
    val pendingSlots by uploadManager.pendingSlots.collectAsStateWithLifecycle()

    // Merge manager pending slots with local uploading slots.
    // Exclude slots that already have a photo URL — the upload is confirmed even if
    // pendingSlots hasn't been cleared yet (race between state update and finally block).
    val allUploadingSlots = (state.uploadingSlots + pendingSlots)
        .filter { state.photos.getOrNull(it) == null }
        .toSet()

    var showTipsSheet by remember { mutableStateOf(false) }
    val tipsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val emptySlots = (0 until 6).filter { state.photos.getOrNull(it) == null && it !in allUploadingSlots }
    val launcher = rememberMultiImagePickerLauncher(
        maxSelection = emptySlots.size.coerceAtLeast(2)
    ) { pickedImages ->
        val currentEmpty = (0 until 6).filter { state.photos.getOrNull(it) == null && it !in allUploadingSlots }
        val requests = pickedImages.zip(currentEmpty).mapNotNull { (image, slot) ->
            val mime = image.mimeType ?: return@mapNotNull null
            PhotoUploadRequest(bytes = image.bytes, mimeType = mime, slotIndex = slot)
        }
        if (requests.isNotEmpty()) {
            uploadManager.enqueue(requests)
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.photo_onboarding_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.photo_onboarding_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { showTipsSheet = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.photo_tips_button),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PhotoGrid(
                photos = state.photos,
                uploadingSlots = allUploadingSlots,
                deletingSlots = emptySet(),
                onPhotoSlotClicked = { index ->
                    if (state.photos.getOrNull(index) == null) {
                        launcher.launch()
                    }
                }
            )

            val imageError = state.imageError
            if (imageError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = imageError.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.photo_onboarding_photos_count, state.uploadedCount),
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.hasMinimumPhotos) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (state.hasMinimumPhotos) FontWeight.SemiBold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            ChirpButton(
                text = stringResource(Res.string.photo_onboarding_continue),
                onClick = { viewModel.onComplete() },
                enabled = state.hasMinimumPhotos && !state.isCompleting,
                isLoading = state.isCompleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(52.dp)
            )
        }
    }

    if (showTipsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTipsSheet = false },
            sheetState = tipsSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            PhotoTipsSheet(onDismiss = { showTipsSheet = false })
        }
    }
}

@Composable
private fun PhotoTipsSheet(onDismiss: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(Res.string.photo_tips_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.photo_tips_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))

        PhotoTipItem(
            icon = Icons.Default.Person,
            iconTint = primary,
            title = stringResource(Res.string.photo_tips_tip1_title),
            description = stringResource(Res.string.photo_tips_tip1_desc)
        )
        PhotoTipItem(
            icon = Icons.Default.CalendarMonth,
            iconTint = primary,
            title = stringResource(Res.string.photo_tips_tip2_title),
            description = stringResource(Res.string.photo_tips_tip2_desc)
        )
        PhotoTipItem(
            icon = Icons.Default.Groups,
            iconTint = error,
            title = stringResource(Res.string.photo_tips_tip3_title),
            description = stringResource(Res.string.photo_tips_tip3_desc)
        )
        PhotoTipItem(
            icon = Icons.Default.VerifiedUser,
            iconTint = primary,
            title = stringResource(Res.string.photo_tips_tip4_title),
            description = stringResource(Res.string.photo_tips_tip4_desc)
        )
        PhotoTipItem(
            icon = Icons.Default.VisibilityOff,
            iconTint = error,
            title = stringResource(Res.string.photo_tips_tip5_title),
            description = stringResource(Res.string.photo_tips_tip5_desc)
        )
        PhotoTipItem(
            icon = Icons.Default.Shield,
            iconTint = primary,
            title = stringResource(Res.string.photo_tips_tip6_title),
            description = stringResource(Res.string.photo_tips_tip6_desc)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ChirpButton(
            text = stringResource(Res.string.photo_tips_got_it),
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
    }
}

@Composable
private fun PhotoTipItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
