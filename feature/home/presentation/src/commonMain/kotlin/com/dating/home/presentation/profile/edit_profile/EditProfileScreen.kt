package com.dating.home.presentation.profile.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.add_photo
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete
import aura.feature.home.presentation.generated.resources.delete_profile_picture
import aura.feature.home.presentation.generated.resources.delete_profile_picture_desc
import aura.feature.home.presentation.generated.resources.edit_profile_about_me
import aura.feature.home.presentation.generated.resources.edit_profile_about_me_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_bio_counter
import aura.feature.home.presentation.generated.resources.edit_profile_birth_date
import aura.feature.home.presentation.generated.resources.edit_profile_birth_date_select
import aura.feature.home.presentation.generated.resources.edit_profile_company
import aura.feature.home.presentation.generated.resources.edit_profile_company_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_drinking
import aura.feature.home.presentation.generated.resources.edit_profile_education
import aura.feature.home.presentation.generated.resources.edit_profile_education_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_gender
import aura.feature.home.presentation.generated.resources.edit_profile_gender_female
import aura.feature.home.presentation.generated.resources.edit_profile_gender_male
import aura.feature.home.presentation.generated.resources.edit_profile_gender_other
import aura.feature.home.presentation.generated.resources.edit_profile_height
import aura.feature.home.presentation.generated.resources.edit_profile_interests
import aura.feature.home.presentation.generated.resources.edit_profile_job
import aura.feature.home.presentation.generated.resources.edit_profile_job_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_photos
import aura.feature.home.presentation.generated.resources.edit_profile_save_success
import aura.feature.home.presentation.generated.resources.edit_profile_smoking
import aura.feature.home.presentation.generated.resources.edit_profile_title
import aura.feature.home.presentation.generated.resources.edit_profile_zodiac
import aura.feature.home.presentation.generated.resources.profile_image
import aura.feature.home.presentation.generated.resources.remove
import aura.feature.home.presentation.generated.resources.save
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.chips.ChirpChip
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.components.textfields.ChirpTextField
import com.dating.core.presentation.util.clearFocusOnTap
import com.dating.home.domain.upload.PhotoUploadEvent
import com.dating.home.domain.upload.PhotoUploadManager
import com.dating.home.domain.upload.PhotoUploadRequest
import com.dating.home.presentation.profile.mediapicker.rememberImagePickerLauncher
import com.dating.home.presentation.profile.mediapicker.rememberMultiImagePickerLauncher
import org.koin.compose.koinInject
import kotlin.time.Clock.System.now
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
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
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(Res.string.edit_profile_save_success)
    val uploadManager: PhotoUploadManager = koinInject()
    val pendingSlots by uploadManager.pendingSlots.collectAsStateWithLifecycle()

    val allUploadingSlots = state.uploadingSlots + pendingSlots

    // Track which slot the user intends to fill — set before launching picker
    var pendingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    // Listen for background upload events
    LaunchedEffect(Unit) {
        uploadManager.events.collect { event ->
            when (event) {
                is PhotoUploadEvent.Success -> viewModel.onAction(
                    EditProfileAction.OnPhotoUploaded(event.slotIndex, event.publicUrl)
                )
                is PhotoUploadEvent.Failed -> viewModel.onAction(
                    EditProfileAction.OnPhotoUploadFailed(event.slotIndex)
                )
            }
        }
    }

    // Single picker for replacing an existing photo
    val singleLauncher = rememberImagePickerLauncher { pickedImageData ->
        pendingSlotIndex?.let { index ->
            viewModel.onAction(
                EditProfileAction.OnPictureSelected(pickedImageData.bytes, pickedImageData.mimeType, index)
            )
        }
        pendingSlotIndex = null
    }

    // Multi picker for empty slots
    val emptySlots = (0 until 6).filter { state.photos[it] == null && it !in allUploadingSlots }
    val multiLauncher = rememberMultiImagePickerLauncher(
        maxSelection = emptySlots.size.coerceAtLeast(1)
    ) { pickedImages ->
        val currentEmpty = (0 until 6).filter { state.photos[it] == null && it !in allUploadingSlots }
        val requests = pickedImages.zip(currentEmpty).mapNotNull { (image, slot) ->
            val mime = image.mimeType ?: return@mapNotNull null
            PhotoUploadRequest(bytes = image.bytes, mimeType = mime, slotIndex = slot)
        }
        if (requests.isNotEmpty()) {
            uploadManager.enqueue(requests)
        }
    }

    fun onPhotoSlotClicked(index: Int) {
        pendingSlotIndex = index
        if (state.photos.getOrNull(index) != null) {
            showPhotoOptionsDialog = true
        } else {
            multiLauncher.launch()
        }
    }

    LaunchedEffect(state.showSuccessMessage) {
        if (state.showSuccessMessage) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.onAction(EditProfileAction.OnDismissSuccessMessage)
        }
    }

    val maxBirthDateMillis = now().toEpochMilliseconds() - 18L * 365L * 24L * 3600L * 1000L
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.birthDate?.toDateMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= maxBirthDateMillis
        }
    )
    LaunchedEffect(state.birthDate) {
        state.birthDate?.toDateMillis()?.let { datePickerState.selectedDateMillis = it }
    }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.edit_profile_title),
                onBack = onBack
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                val saveError = state.saveError
                if (saveError != null) {
                    Text(
                        text = saveError.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = { viewModel.onAction(EditProfileAction.OnSaveProfile) },
                    enabled = !state.isSavingProfile,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSavingProfile) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(stringResource(Res.string.save))
                }
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

            // Photos
            SectionTitle(stringResource(Res.string.edit_profile_photos))
            Spacer(Modifier.height(12.dp))
            PhotoGrid(
                photos = state.photos,
                uploadingSlots = allUploadingSlots,
                deletingSlots = state.deletingSlots,
                onPhotoSlotClicked = { index -> onPhotoSlotClicked(index) },
                onPhotosReordered = { newPhotos -> viewModel.onAction(EditProfileAction.OnPhotosReordered(newPhotos)) }
            )
            val imageError = state.imageError
            if (imageError != null) {
                Text(
                    text = imageError.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            // Bio
            SectionTitle(stringResource(Res.string.edit_profile_about_me))
            Spacer(Modifier.height(12.dp))
            val bioLength = state.bioTextState.text.length
            ChirpTextField(
                state = state.bioTextState,
                placeholder = stringResource(Res.string.edit_profile_about_me_placeholder),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 5
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val bioError = state.bioError
                if (bioError != null) {
                    Text(text = bioError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Text(
                    text = stringResource(Res.string.edit_profile_bio_counter, bioLength),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (bioLength > 500) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))

            // Gender
            SectionTitle(stringResource(Res.string.edit_profile_gender))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("MALE" to stringResource(Res.string.edit_profile_gender_male),
                    "FEMALE" to stringResource(Res.string.edit_profile_gender_female),
                    "OTHER" to stringResource(Res.string.edit_profile_gender_other)
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = state.gender == value,
                        onClick = {
                            viewModel.onAction(
                                EditProfileAction.OnGenderChanged(if (state.gender == value) null else value)
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Birth Date
            SectionTitle(stringResource(Res.string.edit_profile_birth_date))
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = state.birthDate?.toDisplayDate()
                        ?: stringResource(Res.string.edit_profile_birth_date_select)
                )
            }
            val birthDateError = state.birthDateError
            if (birthDateError != null) {
                Text(text = birthDateError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(24.dp))

            // Interests
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(stringResource(Res.string.edit_profile_interests))
                Text(
                    text = "${state.selectedInterests.size}/10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Text(
                    text = interestsError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            // Job / Company / Education
            SectionTitle(stringResource(Res.string.edit_profile_job))
            Spacer(Modifier.height(12.dp))
            ChirpTextField(state = state.jobTitleTextState, placeholder = stringResource(Res.string.edit_profile_job_placeholder))
            Spacer(Modifier.height(16.dp))
            SectionTitle(stringResource(Res.string.edit_profile_company))
            Spacer(Modifier.height(12.dp))
            ChirpTextField(state = state.companyTextState, placeholder = stringResource(Res.string.edit_profile_company_placeholder))
            Spacer(Modifier.height(16.dp))
            SectionTitle(stringResource(Res.string.edit_profile_education))
            Spacer(Modifier.height(12.dp))
            ChirpTextField(state = state.educationTextState, placeholder = stringResource(Res.string.edit_profile_education_placeholder))
            Spacer(Modifier.height(24.dp))

            // Height
            SectionTitle(stringResource(Res.string.edit_profile_height))
            Spacer(Modifier.height(12.dp))
            val height = state.height
            if (height == null) {
                OutlinedButton(
                    onClick = { viewModel.onAction(EditProfileAction.OnHeightChanged(170)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Añadir altura") }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = height.toFloat(),
                        onValueChange = { viewModel.onAction(EditProfileAction.OnHeightChanged(it.toInt())) },
                        valueRange = 100f..250f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$height cm", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(52.dp))
                    IconButton(onClick = { viewModel.onAction(EditProfileAction.OnHeightChanged(null)) }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar altura")
                    }
                }
            }
            val heightError = state.heightError
            if (heightError != null) {
                Text(text = heightError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))

            // Zodiac
            SectionTitle(stringResource(Res.string.edit_profile_zodiac))
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ARIES","TAURUS","GEMINI","CANCER","LEO","VIRGO","LIBRA","SCORPIO","SAGITTARIUS","CAPRICORN","AQUARIUS","PISCES").forEach { zodiac ->
                    FilterChip(
                        selected = state.zodiac == zodiac,
                        onClick = { viewModel.onAction(EditProfileAction.OnZodiacChanged(if (state.zodiac == zodiac) null else zodiac)) },
                        label = { Text(zodiac.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Smoking
            SectionTitle(stringResource(Res.string.edit_profile_smoking))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("NEVER" to "Nunca", "SOMETIMES" to "A veces", "REGULARLY" to "Regularmente").forEach { (value, label) ->
                    FilterChip(
                        selected = state.smoking == value,
                        onClick = { viewModel.onAction(EditProfileAction.OnSmokingChanged(if (state.smoking == value) null else value)) },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            // Drinking
            SectionTitle(stringResource(Res.string.edit_profile_drinking))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("NEVER" to "Nunca", "SOCIALLY" to "Socialmente", "REGULARLY" to "Regularmente").forEach { (value, label) ->
                    FilterChip(
                        selected = state.drinking == value,
                        onClick = { viewModel.onAction(EditProfileAction.OnDrinkingChanged(if (state.drinking == value) null else value)) },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(100.dp))
        }

        // Photo options dialog: change or delete for a filled slot
        if (showPhotoOptionsDialog) {
            AlertDialog(
                onDismissRequest = {
                    showPhotoOptionsDialog = false
                    pendingSlotIndex = null
                },
                title = { Text(stringResource(Res.string.profile_image)) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                showPhotoOptionsDialog = false
                                singleLauncher.launch()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cambiar foto", modifier = Modifier.fillMaxWidth())
                        }
                        TextButton(
                            onClick = {
                                showPhotoOptionsDialog = false
                                pendingSlotIndex?.let { index ->
                                    viewModel.onAction(EditProfileAction.OnDeletePhotoClick(index))
                                }
                                pendingSlotIndex = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Eliminar foto",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = {
                        showPhotoOptionsDialog = false
                        pendingSlotIndex = null
                    }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }

        // Delete confirmation dialog
        if (state.showDeleteConfirmationDialog) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.delete_profile_picture),
                description = stringResource(Res.string.delete_profile_picture_desc),
                confirmButtonText = stringResource(Res.string.delete),
                cancelButtonText = stringResource(Res.string.cancel),
                onConfirmClick = { viewModel.onAction(EditProfileAction.OnConfirmDeletePhoto) },
                onCancelClick = { viewModel.onAction(EditProfileAction.OnDismissDeleteConfirmationDialogClick) },
                onDismiss = { viewModel.onAction(EditProfileAction.OnDismissDeleteConfirmationDialogClick) }
            )
        }

        // Date picker dialog
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
}

// --- Interest helpers ---

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

// --- Date helpers ---

private fun String.toDateMillis(): Long? = try {
    val date = LocalDate.parse(this)
    date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
} catch (_: Exception) { null }

private fun Long.toIsoDate(): String {
    val date = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
    return "${date.year}-${date.month.number.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}"
}

private fun String.toDisplayDate(): String = try {
    val date = LocalDate.parse(this)
    "${date.day} ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)} ${date.year}"
} catch (_: Exception) { this }

// --- Reusable components ---

@Composable
fun SectionTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
fun PhotoGrid(
    photos: List<String?>,
    uploadingSlots: Set<Int>,
    deletingSlots: Set<Int>,
    onPhotoSlotClicked: (Int) -> Unit,
    onPhotosReordered: (List<String?>) -> Unit = {}
) {
    var dragIndex by remember { mutableStateOf<Int?>(null) }
    var hoverIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val slotBounds = remember { mutableStateMapOf<Int, Rect>() }
    var containerCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current

    val liftScale by animateFloatAsState(
        targetValue = if (dragIndex != null) 1.07f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .onGloballyPositioned { containerCoords = it }
                .pointerInput(photos) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset ->
                            dragIndex = slotBounds.entries
                                .firstOrNull { (_, rect) -> rect.contains(offset) }
                                ?.takeIf { photos.getOrNull(it.key) != null }?.key
                            dragOffset = offset
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragOffset = change.position
                            hoverIndex = slotBounds.entries
                                .firstOrNull { (_, rect) -> rect.contains(change.position) }?.key
                        },
                        onDragEnd = {
                            val from = dragIndex
                            val to = hoverIndex
                            if (from != null && to != null && from != to) {
                                val newPhotos = photos.toMutableList()
                                val tmp = newPhotos[from]
                                newPhotos[from] = newPhotos[to]
                                newPhotos[to] = tmp
                                onPhotosReordered(newPhotos)
                            }
                            dragIndex = null
                            hoverIndex = null
                        },
                        onDragCancel = {
                            dragIndex = null
                            hoverIndex = null
                        }
                    )
                }
        ) {
            for (row in 0..1) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        PhotoSlot(
                            imageUrl = photos.getOrNull(index),
                            isLoading = index in uploadingSlots || index in deletingSlots,
                            isDragging = dragIndex == index,
                            isHovered = hoverIndex == index && dragIndex != index,
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coords ->
                                    val container = containerCoords ?: return@onGloballyPositioned
                                    val pos = coords.positionInWindow() - container.positionInWindow()
                                    slotBounds[index] = Rect(
                                        offset = pos,
                                        size = Size(coords.size.width.toFloat(), coords.size.height.toFloat())
                                    )
                                },
                            onSlotClicked = { if (dragIndex == null) onPhotoSlotClicked(index) }
                        )
                    }
                }
            }
        }

        // Floating drag item that follows the finger
        val currentDragIndex = dragIndex
        if (currentDragIndex != null) {
            val photo = photos.getOrNull(currentDragIndex)
            val bound = slotBounds[currentDragIndex]
            if (photo != null && bound != null) {
                val widthDp = with(density) { bound.width.toDp() }
                val heightDp = with(density) { bound.height.toDp() }
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (dragOffset.x - bound.width / 2).roundToInt(),
                                (dragOffset.y - bound.height / 2).roundToInt()
                            )
                        }
                        .size(width = widthDp, height = heightDp)
                        .graphicsLayer {
                            scaleX = liftScale
                            scaleY = liftScale
                            shadowElevation = 24f * (liftScale - 1f) / 0.07f
                            shape = RoundedCornerShape(16.dp)
                            clip = true
                        }
                ) {
                    AsyncImage(
                        model = photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoSlot(
    imageUrl: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    isHovered: Boolean = false,
    onSlotClicked: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 150)
    )
    val contentScale by animateFloatAsState(
        targetValue = if (isHovered) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
    )

    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
                alpha = if (isDragging) 0f else 1f
            }
            .clickable(enabled = !isLoading && !isDragging, onClick = onSlotClicked),
        contentAlignment = Alignment.Center
    ) {
        // Dashed placeholder shown when the slot is the drag origin
        if (isDragging) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            )
        }
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Edit indicator in bottom-right corner (only when not loading)
            if (!isLoading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else if (!isLoading) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(Res.string.add_photo),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
