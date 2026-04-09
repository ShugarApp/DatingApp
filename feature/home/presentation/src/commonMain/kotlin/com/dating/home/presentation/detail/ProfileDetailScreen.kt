package com.dating.home.presentation.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.action_block_desc
import aura.feature.home.presentation.generated.resources.action_delete_match_desc
import aura.feature.home.presentation.generated.resources.action_report_desc
import aura.feature.home.presentation.generated.resources.block_after_report_confirm
import aura.feature.home.presentation.generated.resources.block_after_report_desc
import aura.feature.home.presentation.generated.resources.block_after_report_dismiss
import aura.feature.home.presentation.generated.resources.block_after_report_title
import aura.feature.home.presentation.generated.resources.block_user
import aura.feature.home.presentation.generated.resources.block_user_desc
import aura.feature.home.presentation.generated.resources.block_user_title
import aura.feature.home.presentation.generated.resources.cancel
import aura.feature.home.presentation.generated.resources.delete_match
import aura.feature.home.presentation.generated.resources.delete_match_desc
import aura.feature.home.presentation.generated.resources.delete_match_title
import aura.feature.home.presentation.generated.resources.go_back
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_never
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_regularly
import aura.feature.home.presentation.generated.resources.edit_profile_drinking_socially
import aura.feature.home.presentation.generated.resources.edit_profile_gender_female
import aura.feature.home.presentation.generated.resources.edit_profile_gender_male
import aura.feature.home.presentation.generated.resources.edit_profile_gender_other
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
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_casual
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_friends
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_hookup
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_long_term
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_open
import aura.feature.home.presentation.generated.resources.edit_profile_looking_for_short_term
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_never
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_regularly
import aura.feature.home.presentation.generated.resources.edit_profile_smoking_sometimes
import aura.feature.home.presentation.generated.resources.profile_about_me
import aura.feature.home.presentation.generated.resources.profile_basic_info
import aura.feature.home.presentation.generated.resources.profile_ideal_date
import aura.feature.home.presentation.generated.resources.profile_looking_for
import aura.feature.home.presentation.generated.resources.profile_interests
import aura.feature.home.presentation.generated.resources.profile_like
import aura.feature.home.presentation.generated.resources.profile_pass
import aura.feature.home.presentation.generated.resources.profile_verified
import aura.feature.home.presentation.generated.resources.profile_work_education
import aura.feature.home.presentation.generated.resources.report_duplicate
import aura.feature.home.presentation.generated.resources.report_success
import aura.feature.home.presentation.generated.resources.report_user
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.dating.core.designsystem.theme.extended
import com.dating.core.domain.auth.User
import com.dating.core.presentation.util.ObserveAsEvents
import com.dating.home.presentation.home.swipe.components.MatchCelebrationOverlay
import com.dating.home.presentation.report.ReportUserBottomSheet
import kotlin.time.Clock
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    userId: String,
    imageUrl: String?,
    onBack: () -> Unit,
    onSwipedUser: (String, Boolean) -> Unit = { _, _ -> },
    onUserBlocked: (String) -> Unit = {},
    onForceLogout: () -> Unit = {},
    isOwnProfile: Boolean = false,
    isMatch: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: ProfileDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showMatchDialog by remember { mutableStateOf(false) }
    var matchName by remember { mutableStateOf("") }
    var matchedUserPhotoUrl by remember { mutableStateOf<String?>(null) }
    var currentUserPhotoUrl by remember { mutableStateOf<String?>(null) }
    var swipedUserId by remember { mutableStateOf<String?>(null) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ProfileDetailEvent.NavigateBack -> {
                event.swipedUserId?.let { onSwipedUser(it, event.isDislike) }
                onBack()
            }
            is ProfileDetailEvent.ShowMatch -> {
                swipedUserId = event.swipedUserId
                matchName = event.userName
                matchedUserPhotoUrl = event.matchedUserPhotoUrl
                currentUserPhotoUrl = event.currentUserPhotoUrl
                showMatchDialog = true
            }
            ProfileDetailEvent.OnUserBlocked -> {
                onUserBlocked(userId)
                onBack()
            }
            ProfileDetailEvent.OnMatchDeleted -> onBack()
            is ProfileDetailEvent.OnReportSuccess -> {
                snackbarState.showSnackbar(
                    org.jetbrains.compose.resources.getString(Res.string.report_success)
                )
            }
            is ProfileDetailEvent.OnReportError -> {
                snackbarState.showSnackbar(
                    org.jetbrains.compose.resources.getString(Res.string.report_duplicate)
                )
            }
            ProfileDetailEvent.OnForceLogout -> onForceLogout()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.user != null -> {
                    ProfileDetailContent(
                        user = state.user!!,
                        fallbackImageUrl = imageUrl,
                        onBack = onBack,
                        hideActions = isOwnProfile || isMatch,
                        showBlockButton = !isOwnProfile,
                        showDeleteMatchButton = isMatch,
                        showReportButton = !isOwnProfile,
                        onSwipeLeft = { viewModel.onAction(ProfileDetailAction.OnSwipeLeft(userId)) },
                        onSwipeRight = { viewModel.onAction(ProfileDetailAction.OnSwipeRight(userId)) },
                        onBlockClick = { viewModel.onAction(ProfileDetailAction.OnBlockClick(userId)) },
                        onDeleteMatchClick = { viewModel.onAction(ProfileDetailAction.OnDeleteMatchClick(userId)) },
                        onReportClick = { viewModel.onAction(ProfileDetailAction.OnReportClick(userId)) }
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error?.asString() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUser(userId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }

    if (state.showBlockDialog) {
        val blockUsername = state.user?.username ?: ""
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.block_user_title, blockUsername),
            description = stringResource(Res.string.block_user_desc),
            confirmButtonText = stringResource(Res.string.block_user),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(ProfileDetailAction.OnConfirmBlock) },
            onCancelClick = { viewModel.onAction(ProfileDetailAction.OnDismissBlockDialog) },
            onDismiss = { viewModel.onAction(ProfileDetailAction.OnDismissBlockDialog) }
        )
    }

    if (state.showDeleteMatchDialog) {
        val deleteUsername = state.user?.username ?: ""
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_match_title, deleteUsername),
            description = stringResource(Res.string.delete_match_desc),
            confirmButtonText = stringResource(Res.string.delete_match),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = { viewModel.onAction(ProfileDetailAction.OnConfirmDeleteMatch) },
            onCancelClick = { viewModel.onAction(ProfileDetailAction.OnDismissDeleteMatchDialog) },
            onDismiss = { viewModel.onAction(ProfileDetailAction.OnDismissDeleteMatchDialog) }
        )
    }

    if (state.showBlockAfterReportDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.block_after_report_title),
            description = stringResource(Res.string.block_after_report_desc),
            confirmButtonText = stringResource(Res.string.block_after_report_confirm),
            cancelButtonText = stringResource(Res.string.block_after_report_dismiss),
            onConfirmClick = { viewModel.onAction(ProfileDetailAction.OnConfirmBlockAfterReport) },
            onCancelClick = { viewModel.onAction(ProfileDetailAction.OnDismissBlockAfterReportDialog) },
            onDismiss = { viewModel.onAction(ProfileDetailAction.OnDismissBlockAfterReportDialog) }
        )
    }

    if (state.showReportSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(ProfileDetailAction.OnDismissReportSheet) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            ReportUserBottomSheet(
                isSubmitting = state.isSubmittingReport,
                onSubmit = { reason, description ->
                    viewModel.onAction(ProfileDetailAction.OnSubmitReport(reason, description))
                }
            )
        }
    }

    if (showMatchDialog) {
        MatchCelebrationOverlay(
            currentUserPhotoUrl = currentUserPhotoUrl,
            matchedUserName = matchName,
            matchedUserPhotoUrl = matchedUserPhotoUrl,
            onSendMessage = {
                showMatchDialog = false
                swipedUserId?.let { onSwipedUser(it, false) }
                onBack()
            },
            onKeepSwiping = {
                showMatchDialog = false
                swipedUserId?.let { onSwipedUser(it, false) }
                onBack()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDetailContent(
    user: User,
    fallbackImageUrl: String?,
    onBack: () -> Unit,
    hideActions: Boolean = false,
    showBlockButton: Boolean = false,
    showDeleteMatchButton: Boolean = false,
    showReportButton: Boolean = false,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onBlockClick: () -> Unit = {},
    onDeleteMatchClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
) {
    val photos = user.photos.ifEmpty { listOfNotNull(fallbackImageUrl) }
    val pagerState = rememberPagerState { photos.size.coerceAtLeast(1) }
    val location = listOfNotNull(user.city, user.country).joinToString(", ")
    val age = user.birthDate?.let { calculateAge(it) }
    val scope = rememberCoroutineScope()

    val genderLabels = mapOf(
        "MALE" to stringResource(Res.string.edit_profile_gender_male),
        "FEMALE" to stringResource(Res.string.edit_profile_gender_female),
        "OTHER" to stringResource(Res.string.edit_profile_gender_other)
    )
    val smokingLabels = mapOf(
        "NEVER" to stringResource(Res.string.edit_profile_smoking_never),
        "SOMETIMES" to stringResource(Res.string.edit_profile_smoking_sometimes),
        "REGULARLY" to stringResource(Res.string.edit_profile_smoking_regularly)
    )
    val drinkingLabels = mapOf(
        "NEVER" to stringResource(Res.string.edit_profile_drinking_never),
        "SOCIALLY" to stringResource(Res.string.edit_profile_drinking_socially),
        "REGULARLY" to stringResource(Res.string.edit_profile_drinking_regularly)
    )
    val lookingForLabels = mapOf(
        "LONG_TERM" to stringResource(Res.string.edit_profile_looking_for_long_term),
        "SHORT_TERM" to stringResource(Res.string.edit_profile_looking_for_short_term),
        "CASUAL_DATES" to stringResource(Res.string.edit_profile_looking_for_casual),
        "HOOKUP" to stringResource(Res.string.edit_profile_looking_for_hookup),
        "FRIENDS" to stringResource(Res.string.edit_profile_looking_for_friends),
        "OPEN_TO_ANYTHING" to stringResource(Res.string.edit_profile_looking_for_open)
    )
    val idealDateLabels = mapOf(
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
    var showFullscreenPreview by remember { mutableStateOf(false) }
    var showActionsSheet by remember { mutableStateOf(false) }

    val swipeOffsetX = remember { Animatable(0f) }
    val swipeRotation = remember { Animatable(0f) }
    val swipeAlpha = remember { Animatable(1f) }
    var swipeDirection by remember { mutableStateOf(0) } // -1 = left, 1 = right, 0 = none
    var hasSwiped by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val screenWidthPx = with(density) { 400.dp.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = swipeOffsetX.value
                    rotationZ = swipeRotation.value
                    alpha = swipeAlpha.value
                }
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (hideActions) 16.dp else 100.dp)
        ) {
            // ── Photo pager ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (photos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        val third = size.width / 3
                                        when {
                                            offset.x < third -> {
                                                if (pagerState.currentPage > 0) {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                    }
                                                }
                                            }
                                            offset.x > third * 2 -> {
                                                if (pagerState.currentPage < photos.size - 1) {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                    }
                                                }
                                            }
                                            else -> {
                                                showFullscreenPreview = true
                                            }
                                        }
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = photos[page],
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 300f
                            )
                        )
                )

                // Photo indicator bars
                if (photos.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 52.dp, start = 48.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        photos.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .background(
                                        color = if (index == pagerState.currentPage) Color.White
                                        else Color.White.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }

                // Name, age & location
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (age != null) "${user.username}, $age" else user.username,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        if (user.verificationStatus == com.dating.core.domain.auth.VerificationStatus.VERIFIED) {
                            Spacer(Modifier.width(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        com.dating.home.presentation.components.VerifiedBlue,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                com.dating.home.presentation.components.VerifiedBadge(
                                    size = 13.dp,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = stringResource(Res.string.profile_verified),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    if (location.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Back button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 48.dp, start = 16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.go_back),
                        tint = Color.White
                    )
                }

                // More options button (top-right)
                if (showBlockButton || showDeleteMatchButton || showReportButton) {
                    IconButton(
                        onClick = { showActionsSheet = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 48.dp, end = 16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                }
            }

            // ── Content sections ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // About me
                val bio = user.bio
                if (!bio.isNullOrBlank()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_about_me))
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.extended.textPrimary
                        )
                    }
                }

                // Basic info chips
                val basicInfoItems = buildList {
                    if (age != null) add(Pair(Icons.Default.Cake, "$age años"))
                    user.gender?.let { add(Pair(Icons.Default.Person, genderLabels[it] ?: it.lowercase().replaceFirstChar { c -> c.uppercase() })) }
                    user.height?.let { add(Pair(Icons.Default.Straighten, "$it cm")) }
                    user.zodiac?.let { add(Pair(Icons.Default.Star, it.lowercase().replaceFirstChar { c -> c.uppercase() })) }
                    user.smoking?.let { add(Pair(Icons.Default.SmokingRooms, smokingLabels[it] ?: it.lowercase().replaceFirstChar { c -> c.uppercase() })) }
                    user.drinking?.let { add(Pair(Icons.Default.LocalBar, drinkingLabels[it] ?: it.lowercase().replaceFirstChar { c -> c.uppercase() })) }
                }
                if (basicInfoItems.isNotEmpty()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_basic_info))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            basicInfoItems.forEach { (icon, label) ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(label) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Interests
                if (user.interests.isNotEmpty()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_interests))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            user.interests.forEach { interest ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(interest) }
                                )
                            }
                        }
                    }
                }

                // Work & Education
                val workItems = buildList {
                    if (!user.jobTitle.isNullOrBlank() || !user.company.isNullOrBlank()) {
                        val workText = listOfNotNull(user.jobTitle, user.company).joinToString(" @ ")
                        add(Pair(Icons.Default.Work, workText))
                    }
                    user.education?.takeIf { it.isNotBlank() }?.let {
                        add(Pair(Icons.Default.School, it))
                    }
                }
                if (workItems.isNotEmpty()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_work_education))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            workItems.forEach { (icon, text) ->
                                ProfileDetailItem(icon = icon, text = text)
                            }
                        }
                    }
                }

                // Looking For
                val lookingForLabel = user.lookingFor?.let { lookingForLabels[it] ?: it.lowercase().replaceFirstChar { c -> c.uppercase() } }
                if (!lookingForLabel.isNullOrBlank()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_looking_for))
                        Text(
                            text = lookingForLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.extended.textPrimary
                        )
                    }
                }

                // Ideal Date
                val idealDateLabel = user.idealDate?.let { idealDateLabels[it] ?: it.lowercase().replaceFirstChar { c -> c.uppercase() } }
                if (!idealDateLabel.isNullOrBlank()) {
                    ProfileSection {
                        SectionTitle(stringResource(Res.string.profile_ideal_date))
                        Text(
                            text = idealDateLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.extended.textPrimary
                        )
                    }
                }
            }
        }

        // ── LIKE / NOPE overlay labels ──────────────────────────────
        if (swipeDirection == 1) {
            Text(
                text = "LIKE",
                color = MaterialTheme.colorScheme.extended.success,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 80.dp, start = 32.dp)
                    .rotate(-20f)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.extended.success,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        if (swipeDirection == -1) {
            Text(
                text = "NOPE",
                color = MaterialTheme.colorScheme.error,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 32.dp)
                    .rotate(20f)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ── Floating action buttons ───────────────────────────────────
        if (!hideActions && !hasSwiped) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(vertical = 24.dp, horizontal = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!hasSwiped) {
                                hasSwiped = true
                                swipeDirection = -1
                                scope.launch {
                                    launch { swipeOffsetX.animateTo(-screenWidthPx * 1.5f, tween(400)) }
                                    launch { swipeRotation.animateTo(-15f, tween(400)) }
                                    launch { swipeAlpha.animateTo(0f, tween(400)) }
                                    onSwipeLeft()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.profile_pass),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!hasSwiped) {
                                hasSwiped = true
                                swipeDirection = 1
                                scope.launch {
                                    launch { swipeOffsetX.animateTo(screenWidthPx * 1.5f, tween(400)) }
                                    launch { swipeRotation.animateTo(15f, tween(400)) }
                                    launch { swipeAlpha.animateTo(0f, tween(400)) }
                                    onSwipeRight()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.extended.success.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(Res.string.profile_like),
                            tint = MaterialTheme.colorScheme.extended.success,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // ── Fullscreen photo preview ──────────────────────────────────
        if (showFullscreenPreview && photos.isNotEmpty()) {
            FullscreenPhotoPreview(
                photos = photos,
                initialPage = pagerState.currentPage,
                onDismiss = { finalPage ->
                    showFullscreenPreview = false
                    scope.launch {
                        pagerState.scrollToPage(finalPage)
                    }
                }
            )
        }
    }

    // ── Actions bottom sheet ──────────────────────────────────────
    if (showActionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ProfileActionsSheet(
                showDeleteMatchButton = showDeleteMatchButton,
                showReportButton = showReportButton,
                showBlockButton = showBlockButton,
                onDeleteMatchClick = {
                    showActionsSheet = false
                    onDeleteMatchClick()
                },
                onReportClick = {
                    showActionsSheet = false
                    onReportClick()
                },
                onBlockClick = {
                    showActionsSheet = false
                    onBlockClick()
                }
            )
        }
    }
}

@Composable
private fun FullscreenPhotoPreview(
    photos: List<String>,
    initialPage: Int,
    onDismiss: (currentPage: Int) -> Unit
) {
    val previewPagerState = rememberPagerState(initialPage = initialPage) { photos.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = previewPagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val third = size.width / 3
                            when {
                                offset.x < third -> {
                                    if (previewPagerState.currentPage > 0) {
                                        scope.launch {
                                            previewPagerState.animateScrollToPage(previewPagerState.currentPage - 1)
                                        }
                                    }
                                }
                                offset.x > third * 2 -> {
                                    if (previewPagerState.currentPage < photos.size - 1) {
                                        scope.launch {
                                            previewPagerState.animateScrollToPage(previewPagerState.currentPage + 1)
                                        }
                                    }
                                }
                                else -> {
                                    onDismiss(previewPagerState.currentPage)
                                }
                            }
                        }
                    }
            ) {
                AsyncImage(
                    model = photos[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Photo indicator bars
        if (photos.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                photos.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                color = if (index == previewPagerState.currentPage) Color.White
                                else Color.White.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        // Close button
        IconButton(
            onClick = { onDismiss(previewPagerState.currentPage) },
            modifier = Modifier
                .padding(top = 48.dp, end = 16.dp)
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ProfileActionsSheet(
    showDeleteMatchButton: Boolean,
    showReportButton: Boolean,
    showBlockButton: Boolean,
    onDeleteMatchClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Options",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )
        if (showReportButton) {
            ProfileActionItem(
                icon = Icons.Default.Flag,
                iconBgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                iconTint = MaterialTheme.colorScheme.onErrorContainer,
                title = stringResource(Res.string.report_user),
                subtitle = stringResource(Res.string.action_report_desc),
                onClick = onReportClick
            )
        }
        if (showBlockButton) {
            ProfileActionItem(
                icon = Icons.Default.Block,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                title = stringResource(Res.string.block_user),
                subtitle = stringResource(Res.string.action_block_desc),
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onBlockClick
            )
        }
        if (showDeleteMatchButton) {
            ProfileActionItem(
                icon = Icons.Default.HeartBroken,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                title = stringResource(Res.string.delete_match),
                subtitle = stringResource(Res.string.action_delete_match_desc),
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onDeleteMatchClick
            )
        }
    }
}

@Composable
private fun ProfileActionItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconBgColor,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = titleColor ?: MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.extended.textPrimary
    )
}

private fun calculateAge(birthDate: String): Int? {
    return try {
        val parts = birthDate.split("-")
        if (parts.size < 3) return null
        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()
        // Simple cross-platform age calculation without kotlinx-datetime
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        var age = now.year - birthYear
        if (now.month.number < birthMonth || (now.month.number == birthMonth && now.day < birthDay)) {
            age--
        }
        age
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ProfileDetailItem(
    icon: ImageVector,
    text: String,
    subtext: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}
