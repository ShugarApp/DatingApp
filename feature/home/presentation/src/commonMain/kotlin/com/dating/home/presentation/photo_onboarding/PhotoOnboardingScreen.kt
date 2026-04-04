package com.dating.home.presentation.photo_onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.photo_onboarding_continue
import aura.feature.home.presentation.generated.resources.photo_onboarding_photos_count
import aura.feature.home.presentation.generated.resources.photo_onboarding_subtitle
import aura.feature.home.presentation.generated.resources.photo_onboarding_title
import com.dating.home.domain.upload.PhotoUploadManager
import com.dating.home.domain.upload.PhotoUploadRequest
import com.dating.home.presentation.profile.edit_profile.PhotoGrid
import com.dating.home.presentation.profile.mediapicker.rememberMultiImagePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PhotoOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoOnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadManager: PhotoUploadManager = koinInject()
    val pendingSlots by uploadManager.pendingSlots.collectAsStateWithLifecycle()

    // Merge manager pending slots with local uploading slots
    val allUploadingSlots = state.uploadingSlots + pendingSlots

    val emptySlots = (0 until 4).filter { state.photos[it] == null && it !in allUploadingSlots }
    val launcher = rememberMultiImagePickerLauncher(
        maxSelection = emptySlots.size.coerceAtLeast(2)
    ) { pickedImages ->
        val currentEmpty = (0 until 4).filter { state.photos[it] == null && it !in allUploadingSlots }
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

            Spacer(modifier = Modifier.height(32.dp))

            PhotoGrid(
                photos = state.photos,
                uploadingSlots = allUploadingSlots,
                deletingSlots = emptySet(),
                onPhotoSlotClicked = { index ->
                    if (state.photos[index] == null) {
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

            Button(
                onClick = { viewModel.onComplete() },
                enabled = state.hasMinimumPhotos && !state.isCompleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(52.dp)
            ) {
                if (state.isCompleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.photo_onboarding_continue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
