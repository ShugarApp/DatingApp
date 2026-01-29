package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.cancel
import shugar.feature.home.presentation.generated.resources.delete
import shugar.feature.home.presentation.generated.resources.delete_profile_picture
import shugar.feature.home.presentation.generated.resources.delete_profile_picture_desc
import shugar.feature.home.presentation.generated.resources.edit_profile_title
import shugar.feature.home.presentation.generated.resources.save
import shugar.feature.home.presentation.generated.resources.edit_profile_photos
import shugar.feature.home.presentation.generated.resources.edit_profile_about_me
import shugar.feature.home.presentation.generated.resources.edit_profile_about_me_placeholder
import shugar.feature.home.presentation.generated.resources.edit_profile_interests
import shugar.feature.home.presentation.generated.resources.edit_profile_job
import shugar.feature.home.presentation.generated.resources.edit_profile_job_placeholder
import shugar.feature.home.presentation.generated.resources.edit_profile_company
import shugar.feature.home.presentation.generated.resources.edit_profile_company_placeholder
import shugar.feature.home.presentation.generated.resources.edit_profile_education
import shugar.feature.home.presentation.generated.resources.edit_profile_education_placeholder
import shugar.feature.home.presentation.generated.resources.edit_profile_personal_info
import shugar.feature.home.presentation.generated.resources.edit_profile_height
import shugar.feature.home.presentation.generated.resources.edit_profile_zodiac
import shugar.feature.home.presentation.generated.resources.edit_profile_smoking
import shugar.feature.home.presentation.generated.resources.edit_profile_drinking
import shugar.feature.home.presentation.generated.resources.remove
import shugar.feature.home.presentation.generated.resources.add_photo
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.home.presentation.profile.mediapicker.rememberImagePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher = rememberImagePickerLauncher { pickedImageData ->
        viewModel.onAction(EditProfileAction.OnPictureSelected(pickedImageData.bytes, pickedImageData.mimeType))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.edit_profile_title),
                onBack = onBack
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ChirpButton(
                    text = stringResource(Res.string.save),
                    onClick = { onBack() },
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .clearFocusOnTap()
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // -- Photos Section --
            SectionTitle(title = stringResource(Res.string.edit_profile_photos))
            Spacer(modifier = Modifier.height(12.dp))
            PhotoGrid(
                photos = state.photos,
                onAddPhoto = { launcher.launch() },
                onRemovePhoto = { viewModel.onAction(EditProfileAction.OnDeletePictureClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -- About Me Section --
            SectionTitle(title = stringResource(Res.string.edit_profile_about_me))
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.bioTextState,
                placeholder = stringResource(Res.string.edit_profile_about_me_placeholder),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -- Interests Section --
            SectionTitle(title = stringResource(Res.string.edit_profile_interests))
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.availableInterests.forEach { interest ->
                    val isSelected = state.selectedInterests.contains(interest)
                    ChirpChip(
                        text = stringResource(interest),
                        isSelected = isSelected,
                        onClick = { viewModel.onAction(EditProfileAction.OnInterestSelected(interest)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // -- Details Section --
            SectionTitle(title = stringResource(Res.string.edit_profile_job))
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.jobTitleTextState,
                placeholder = stringResource(Res.string.edit_profile_job_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(Res.string.edit_profile_company))
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.companyTextState,
                placeholder = stringResource(Res.string.edit_profile_company_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = stringResource(Res.string.edit_profile_education))
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.educationTextState,
                placeholder = stringResource(Res.string.edit_profile_education_placeholder)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            SectionTitle(title = stringResource(Res.string.edit_profile_personal_info))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChirpTextField(
                    state = state.heightTextState,
                    placeholder = stringResource(Res.string.edit_profile_height),
                    modifier = Modifier.weight(1f)
                )
                ChirpTextField(
                    state = state.zodiacTextState,
                    placeholder = stringResource(Res.string.edit_profile_zodiac),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChirpTextField(
                    state = state.smokingTextState,
                    placeholder = stringResource(Res.string.edit_profile_smoking),
                    modifier = Modifier.weight(1f)
                )
                ChirpTextField(
                    state = state.drinkingTextState,
                    placeholder = stringResource(Res.string.edit_profile_drinking),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for sticky button
        }

        if (state.showDeleteConfirmationDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.delete_profile_picture),
                description = stringResource(Res.string.delete_profile_picture_desc),
                confirmButtonText = stringResource(Res.string.delete),
                cancelButtonText = stringResource(Res.string.cancel),
                onConfirmClick = {
                    viewModel.onAction(EditProfileAction.OnConfirmDeleteClick)
                },
                onCancelClick = {
                    viewModel.onAction(EditProfileAction.OnDismissDeleteConfirmationDialogClick)
                },
                onDismiss = {
                    viewModel.onAction(EditProfileAction.OnDismissDeleteConfirmationDialogClick)
                }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun PhotoGrid(
    photos: List<String?>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Row 1
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PhotoSlot(photos.getOrNull(0), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(0) })
            PhotoSlot(photos.getOrNull(1), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(1) })
            PhotoSlot(photos.getOrNull(2), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(2) })
        }
        // Row 2
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PhotoSlot(photos.getOrNull(3), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(3) })
            PhotoSlot(photos.getOrNull(4), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(4) })
            PhotoSlot(photos.getOrNull(5), modifier = Modifier.weight(1f), onAdd = onAddPhoto, onRemove = { onRemovePhoto(5) })
        }
    }
}

@Composable
fun PhotoSlot(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit,
    onRemove: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(0.75f) // Portrait aspect ratio for photos
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { if (imageUrl == null) onAdd() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Remove Button Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.remove),
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add_photo),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
