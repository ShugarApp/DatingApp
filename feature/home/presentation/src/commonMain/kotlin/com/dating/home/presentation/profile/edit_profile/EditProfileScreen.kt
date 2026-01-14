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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.home.presentation.profile.mediapicker.rememberImagePickerLauncher
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
        viewModel.onAction(EditProfileAction.OnPictureSelected(
            pickedImageData.bytes,
            pickedImageData.mimeType
        ))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = "Edit Profile",
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
                    text = "Save",
                    onClick = { onBack() }, // Mock save action
                    modifier = Modifier.fillMaxWidth()
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
            SectionTitle(title = "Photos")
            Spacer(modifier = Modifier.height(12.dp))
            PhotoGrid(
                photos = state.photos,
                onAddPhoto = { launcher.launch() },
                onRemovePhoto = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // -- About Me Section --
            SectionTitle(title = "About Me")
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.bioTextState,
                placeholder = "Tell us about yourself...",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // -- Interests Section --
            SectionTitle(title = "Interests")
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.availableInterests.forEach { interest ->
                    val isSelected = state.selectedInterests.contains(interest)
                    InterestChip(
                        text = interest,
                        isSelected = isSelected,
                        onClick = { viewModel.onAction(EditProfileAction.OnInterestSelected(interest)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // -- Details Section --
            SectionTitle(title = "Job Title")
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.jobTitleTextState,
                placeholder = "Add job title"
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Company")
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.companyTextState,
                placeholder = "Add company"
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            SectionTitle(title = "Education")
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.educationTextState,
                placeholder = "Add education"
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Location")
            Spacer(modifier = Modifier.height(12.dp))
            ChirpTextField(
                state = state.locationTextState,
                placeholder = "Add location"
            )
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for sticky button
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
           PhotoSlot(photos.getOrNull(0), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
           PhotoSlot(photos.getOrNull(1), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
           PhotoSlot(photos.getOrNull(2), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
        }
        // Row 2
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
           PhotoSlot(photos.getOrNull(3), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
           PhotoSlot(photos.getOrNull(4), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
           PhotoSlot(photos.getOrNull(5), modifier = Modifier.weight(1f), onAdd = onAddPhoto)
        }
    }
}

@Composable
fun PhotoSlot(
    imageUrl: String?, 
    modifier: Modifier = Modifier,
    onAdd: () -> Unit
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
                    .clickable { /* Remove logic */ },
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                     imageVector = Icons.Default.Close,
                     contentDescription = "Remove",
                     tint = Color.White,
                     modifier = Modifier.size(16.dp)
                 )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Photo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun InterestChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
