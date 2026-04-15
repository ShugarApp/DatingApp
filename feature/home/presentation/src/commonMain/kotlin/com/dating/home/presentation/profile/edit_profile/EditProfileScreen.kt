package com.dating.home.presentation.profile.edit_profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.add_photo
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete
import aura.feature.home.presentation.generated.resources.delete_profile_picture
import aura.feature.home.presentation.generated.resources.delete_profile_picture_desc
import aura.feature.home.presentation.generated.resources.edit_profile_about_me
import aura.feature.home.presentation.generated.resources.edit_profile_about_me_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_age_format
import aura.feature.home.presentation.generated.resources.edit_profile_bio_counter
import aura.feature.home.presentation.generated.resources.edit_profile_change_photo
import aura.feature.home.presentation.generated.resources.edit_profile_company
import aura.feature.home.presentation.generated.resources.edit_profile_company_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_delete_photo
import aura.feature.home.presentation.generated.resources.edit_profile_drinking
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_never
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_regularly
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_socially
import aura.feature.home.presentation.generated.resources.edit_profile_education
import aura.feature.home.presentation.generated.resources.edit_profile_education_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_gender_female
import aura.feature.home.presentation.generated.resources.edit_profile_gender_male
import aura.feature.home.presentation.generated.resources.edit_profile_gender_other
import aura.feature.home.presentation.generated.resources.edit_profile_height
import aura.feature.home.presentation.generated.resources.edit_profile_height_add
import aura.feature.home.presentation.generated.resources.edit_profile_height_clear
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_adventure
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_beach
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_cinema
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_coffee
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_concert
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_cooking
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_dinner
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_museum
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_picnic
import aura.feature.home.presentation.generated.resources.edit_profile_ideal_date_travel
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_everyone
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_men
import aura.feature.home.presentation.generated.resources.edit_profile_interested_in_women
import aura.feature.home.presentation.generated.resources.edit_profile_interests
import aura.feature.home.presentation.generated.resources.edit_profile_job
import aura.feature.home.presentation.generated.resources.edit_profile_job_placeholder
import aura.feature.home.presentation.generated.resources.edit_profile_lifestyle
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_casual
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_friends
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_hookup
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_long_term
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_open
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_short_term
import aura.feature.home.presentation.generated.resources.edit_profile_my_preferences
import aura.feature.home.presentation.generated.resources.edit_profile_not_set
import aura.feature.home.presentation.generated.resources.edit_profile_personal_info
import aura.feature.home.presentation.generated.resources.edit_profile_personal_info_note
import aura.feature.home.presentation.generated.resources.edit_profile_photos
import aura.feature.home.presentation.generated.resources.edit_profile_smoking
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_never
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_regularly
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_sometimes
import aura.feature.home.presentation.generated.resources.edit_profile_title
import aura.feature.home.presentation.generated.resources.edit_profile_work_education
import aura.feature.home.presentation.generated.resources.edit_profile_zodiac
import aura.feature.home.presentation.generated.resources.profile_image
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
import kotlin.math.roundToInt
import kotlin.time.Clock.System.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uploadManager: PhotoUploadManager = koinInject()
    val pendingSlots by uploadManager.pendingSlots.collectAsStateWithLifecycle()

    val allUploadingSlots = state.uploadingSlots + pendingSlots

    var pendingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

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

    val singleLauncher = rememberImagePickerLauncher { pickedImageData ->
        pendingSlotIndex?.let { index ->
            viewModel.onAction(
                EditProfileAction.OnPictureSelected(pickedImageData.bytes, pickedImageData.mimeType, index)
            )
        }
        pendingSlotIndex = null
    }

    val emptySlots = (0 until 6).filter { state.photos[it] == null && it !in allUploadingSlots }
    val multiLauncher = rememberMultiImagePickerLauncher(
        maxSelection = emptySlots.size.coerceAtLeast(2)
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
            viewModel.onAction(EditProfileAction.OnDismissSuccessMessage)
            onBack()
        }
    }

    // String resources for options
    val genderMale = stringResource(Res.string.edit_profile_gender_male)
    val genderFemale = stringResource(Res.string.edit_profile_gender_female)
    val genderOther = stringResource(Res.string.edit_profile_gender_other)

    val interestedInOptions = listOf(
        "MALE" to stringResource(Res.string.edit_profile_interested_in_men),
        "FEMALE" to stringResource(Res.string.edit_profile_interested_in_women),
        "EVERYONE" to stringResource(Res.string.edit_profile_interested_in_everyone)
    )

    val lookingForOptions = listOf(
        "Long term" to stringResource(Res.string.edit_profile_looking_for_long_term),
        "Short term" to stringResource(Res.string.edit_profile_looking_for_short_term),
        "Casual dates" to stringResource(Res.string.edit_profile_looking_for_casual),
        "Hookup" to stringResource(Res.string.edit_profile_looking_for_hookup),
        "Friends" to stringResource(Res.string.edit_profile_looking_for_friends),
        "Open to anything" to stringResource(Res.string.edit_profile_looking_for_open)
    )

    val idealDateOptions = listOf(
        "DINNER" to stringResource(Res.string.edit_profile_ideal_date_dinner),
        "COFFEE" to stringResource(Res.string.edit_profile_ideal_date_coffee),
        "ADVENTURE" to stringResource(Res.string.edit_profile_ideal_date_adventure),
        "CINEMA" to stringResource(Res.string.edit_profile_ideal_date_cinema),
        "PICNIC" to stringResource(Res.string.edit_profile_ideal_date_picnic),
        "TRAVEL" to stringResource(Res.string.edit_profile_ideal_date_travel),
        "CONCERT" to stringResource(Res.string.edit_profile_ideal_date_concert),
        "MUSEUM" to stringResource(Res.string.edit_profile_ideal_date_museum),
        "BEACH" to stringResource(Res.string.edit_profile_ideal_date_beach),
        "COOKING" to stringResource(Res.string.edit_profile_ideal_date_cooking)
    )

    val zodiacOptions = listOf(
        "ARIES", "TAURUS", "GEMINI", "CANCER", "LEO", "VIRGO",
        "LIBRA", "SCORPIO", "SAGITTARIUS", "CAPRICORN", "AQUARIUS", "PISCES"
    )

    val smokingOptions = listOf(
        "NEVER" to stringResource(Res.string.edit_profile_smoking_never),
        "SOMETIMES" to stringResource(Res.string.edit_profile_smoking_sometimes),
        "REGULARLY" to stringResource(Res.string.edit_profile_smoking_regularly)
    )

    val drinkingOptions = listOf(
        "NEVER" to stringResource(Res.string.edit_profile_drinking_never),
        "SOCIALLY" to stringResource(Res.string.edit_profile_drinking_socially),
        "REGULARLY" to stringResource(Res.string.edit_profile_drinking_regularly)
    )

    val notSetLabel = stringResource(Res.string.edit_profile_not_set)
    val heightAddLabel = stringResource(Res.string.edit_profile_height_add)
    val heightClearLabel = stringResource(Res.string.edit_profile_height_clear)

    var hasUnsavedChanges by rememberSaveable { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    fun change(action: EditProfileAction) {
        hasUnsavedChanges = true
        viewModel.onAction(action)
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showUnsavedChangesDialog = true
    }

    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to leave without saving?") },
            confirmButton = {
                TextButton(
                    onClick = { showUnsavedChangesDialog = false; onBack() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedChangesDialog = false }) { Text("Keep editing") }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.edit_profile_title),
                onBack = {
                    if (hasUnsavedChanges) showUnsavedChangesDialog = true
                    else onBack()
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isSavingProfile) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            text = if (state.isSavingProfile) "Saving..." else "Save Changes",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
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
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Photos ──────────────────────────────────────────────────────
            EditSection(title = stringResource(Res.string.edit_profile_photos)) {
                PhotoGrid(
                    photos = state.photos,
                    uploadingSlots = allUploadingSlots,
                    deletingSlots = state.deletingSlots,
                    onPhotoSlotClicked = { index -> onPhotoSlotClicked(index) },
                    onPhotosReordered = { newPhotos ->
                        viewModel.onAction(EditProfileAction.OnPhotosReordered(newPhotos))
                    }
                )
                val imageError = state.imageError
                if (imageError != null) {
                    Text(
                        text = imageError.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            SectionDivider()

            // ── About Me ─────────────────────────────────────────────────────
            EditSection(title = stringResource(Res.string.edit_profile_about_me)) {
                val bioLength = state.bioTextState.text.length
                ChirpTextField(
                    state = state.bioTextState,
                    placeholder = stringResource(Res.string.edit_profile_about_me_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 4
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val bioError = state.bioError
                    if (bioError != null) {
                        Text(
                            text = bioError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    Text(
                        text = stringResource(Res.string.edit_profile_bio_counter, bioLength),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (bioLength > 500) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SectionDivider()

            // ── Personal Info (read-only) ─────────────────────────────────────
            EditSection(
                title = stringResource(Res.string.edit_profile_personal_info),
                subtitle = stringResource(Res.string.edit_profile_personal_info_note)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Gender badge
                    val genderLabel = when (state.gender) {
                        "MALE" -> genderMale
                        "FEMALE" -> genderFemale
                        "OTHER" -> genderOther
                        else -> notSetLabel
                    }
                    InfoBadge(label = genderLabel)

                    // Age badge
                    val age = state.birthDate?.toAge()
                    if (age != null) {
                        InfoBadge(label = stringResource(Res.string.edit_profile_age_format, age))
                    } else {
                        InfoBadge(label = notSetLabel)
                    }
                }
            }

            SectionDivider()

            // ── Interests ────────────────────────────────────────────────────
            EditSection(
                title = stringResource(Res.string.edit_profile_interests),
                trailing = {
                    Text(
                        text = "${state.selectedInterests.size}/10",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.availableInterests.forEach { interest ->
                        ChirpChip(
                            text = interestDisplayName(interest),
                            isSelected = state.selectedInterests.contains(interest),
                            onClick = { change(EditProfileAction.OnInterestToggled(interest)) }
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
            }

            SectionDivider()

            // ── My Preferences ───────────────────────────────────────────────
            EditSection(title = stringResource(Res.string.edit_profile_my_preferences)) {

                // Interested In
                SubSectionTitle(stringResource(Res.string.edit_profile_interested_in))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    interestedInOptions.forEach { (value, label) ->
                        ChirpChip(
                            text = label,
                            isSelected = state.interestedIn == value,
                            onClick = {
                                change(
                                    EditProfileAction.OnInterestedInChanged(
                                        if (state.interestedIn == value) null else value
                                    )
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Looking For
                SubSectionTitle(stringResource(Res.string.edit_profile_looking_for))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lookingForOptions.forEach { (value, label) ->
                        ChirpChip(
                            text = label,
                            isSelected = state.lookingFor == value,
                            onClick = {
                                change(
                                    EditProfileAction.OnLookingForChanged(
                                        if (state.lookingFor == value) null else value
                                    )
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Ideal Date
                SubSectionTitle(stringResource(Res.string.edit_profile_ideal_date))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    idealDateOptions.forEach { (value, label) ->
                        ChirpChip(
                            text = label,
                            isSelected = state.idealDate == value,
                            onClick = {
                                change(
                                    EditProfileAction.OnIdealDateChanged(
                                        if (state.idealDate == value) null else value
                                    )
                                )
                            }
                        )
                    }
                }
            }

            SectionDivider()

            // ── Work & Education ─────────────────────────────────────────────
            EditSection(title = stringResource(Res.string.edit_profile_work_education)) {
                SubSectionTitle(stringResource(Res.string.edit_profile_job))
                Spacer(Modifier.height(8.dp))
                ChirpTextField(
                    state = state.jobTitleTextState,
                    placeholder = stringResource(Res.string.edit_profile_job_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                SubSectionTitle(stringResource(Res.string.edit_profile_company))
                Spacer(Modifier.height(8.dp))
                ChirpTextField(
                    state = state.companyTextState,
                    placeholder = stringResource(Res.string.edit_profile_company_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                SubSectionTitle(stringResource(Res.string.edit_profile_education))
                Spacer(Modifier.height(8.dp))
                ChirpTextField(
                    state = state.educationTextState,
                    placeholder = stringResource(Res.string.edit_profile_education_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SectionDivider()

            // ── Lifestyle ────────────────────────────────────────────────────
            EditSection(title = stringResource(Res.string.edit_profile_lifestyle)) {

                // Height
                SubSectionTitle(stringResource(Res.string.edit_profile_height))
                Spacer(Modifier.height(8.dp))
                HeightSelector(
                    height = state.height,
                    addLabel = heightAddLabel,
                    clearLabel = heightClearLabel,
                    onHeightChanged = { change(EditProfileAction.OnHeightChanged(it)) }
                )
                val heightError = state.heightError
                if (heightError != null) {
                    Text(
                        text = heightError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Zodiac
                SubSectionTitle(stringResource(Res.string.edit_profile_zodiac))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    zodiacOptions.forEach { zodiac ->
                        ChirpChip(
                            text = zodiac.lowercase().replaceFirstChar { it.uppercase() },
                            isSelected = state.zodiac == zodiac,
                            onClick = {
                                change(
                                    EditProfileAction.OnZodiacChanged(
                                        if (state.zodiac == zodiac) null else zodiac
                                    )
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Smoking
                SubSectionTitle(stringResource(Res.string.edit_profile_smoking))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    smokingOptions.forEach { (value, label) ->
                        ChirpChip(
                            text = label,
                            isSelected = state.smoking == value,
                            onClick = {
                                change(
                                    EditProfileAction.OnSmokingChanged(
                                        if (state.smoking == value) null else value
                                    )
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Drinking
                SubSectionTitle(stringResource(Res.string.edit_profile_drinking))
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    drinkingOptions.forEach { (value, label) ->
                        ChirpChip(
                            text = label,
                            isSelected = state.drinking == value,
                            onClick = {
                                change(
                                    EditProfileAction.OnDrinkingChanged(
                                        if (state.drinking == value) null else value
                                    )
                                )
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        // Photo options dialog
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
                            Text(
                                stringResource(Res.string.edit_profile_change_photo),
                                modifier = Modifier.fillMaxWidth()
                            )
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
                                text = stringResource(Res.string.edit_profile_delete_photo),
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
    }
}

// ── Height selector ───────────────────────────────────────────────────────────

@Composable
private fun HeightSelector(
    height: Int?,
    addLabel: String,
    clearLabel: String,
    onHeightChanged: (Int?) -> Unit
) {
    if (height == null) {
        // Empty state — tap to initialise at 170 cm
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onHeightChanged(170) },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = addLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                // Top row: value + clear button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$height",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "cm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    TextButton(
                        onClick = { onHeightChanged(null) },
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text(
                            text = clearLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Slider
                Slider(
                    value = height.toFloat(),
                    onValueChange = { onHeightChanged(it.toInt()) },
                    valueRange = 100f..250f,
                    modifier = Modifier.fillMaxWidth()
                )
                // Range labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "100 cm",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "250 cm",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Section layout helpers ────────────────────────────────────────────────────

@Composable
private fun EditSection(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            trailing?.invoke()
        }
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 1.dp
    )
}

@Composable
private fun SubSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun InfoBadge(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 0.dp
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// ── Interest helpers ──────────────────────────────────────────────────────────

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

// ── Date helpers ──────────────────────────────────────────────────────────────

private fun String.toAge(): Int? = try {
    val date = LocalDate.parse(this)
    val today = now().toLocalDateTime(TimeZone.UTC).date
    var age = today.year - date.year
    if (today.monthNumber < date.monthNumber ||
        (today.monthNumber == date.monthNumber && today.dayOfMonth < date.dayOfMonth)
    ) age--
    age
} catch (_: Exception) { null }

// ── Reusable photo components ─────────────────────────────────────────────────

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@OptIn(ExperimentalLayoutApi::class)
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

        // Floating drag item
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
