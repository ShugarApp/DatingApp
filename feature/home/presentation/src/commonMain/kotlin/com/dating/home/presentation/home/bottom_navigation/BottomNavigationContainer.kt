package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.photo_upload_failed
import aura.feature.home.presentation.generated.resources.photo_upload_success
import aura.feature.home.presentation.generated.resources.sos_sent
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.auth.UserStatus
import com.dating.core.domain.preferences.OnboardingPreferences
import com.dating.home.domain.emergency.ShakeDetector
import com.dating.home.domain.emergency.VolumeButtonEventBus
import com.dating.home.domain.emergency.EmergencyContactRepository
import com.dating.home.domain.emergency.EmergencySettingsStorage
import com.dating.home.domain.upload.PhotoUploadEvent
import com.dating.home.domain.upload.PhotoUploadManager
import com.dating.home.presentation.chat.chat_list_detail.ChatListDetailAdaptiveLayout
import com.dating.home.presentation.dates.DatesRoot
import com.dating.home.presentation.dates.DatesViewModel
import com.dating.home.presentation.emergency.contacts.EmergencyContactsAction
import com.dating.home.presentation.emergency.contacts.EmergencyContactsEvent
import com.dating.home.presentation.emergency.contacts.EmergencyContactsViewModel
import com.dating.home.presentation.emergency.contacts.rememberSmsPermissionLauncher
import com.dating.home.presentation.emergency.sos.PanicButton
import com.dating.home.presentation.emergency.sos.SosCountdownDialog
import com.dating.home.presentation.features_onboarding.FeaturesOnboardingScreen
import com.dating.home.presentation.home.swipe.FeedRoot
import com.dating.home.presentation.matches.MatchesRoot
import com.dating.home.presentation.matches.MatchesViewModel
import com.dating.home.presentation.photo_onboarding.PhotoOnboardingScreen
import com.dating.home.presentation.profile.profile.ProfileScreen
import com.dating.home.presentation.profile_setup.ProfileSetupFastScreen
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BottomNavigationContainer(
    onNavigateToProfile: (String, String?) -> Unit,
    onNavigateToMatchProfile: (String, String?) -> Unit,
    onNavigateToOwnProfile: (String, String?) -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    onSafetyCenter: () -> Unit = {},
    onDateSafetyTips: () -> Unit = {},
    onDateSafetyChecklist: () -> Unit = {},
    onSafeDate: () -> Unit = {},
    initialSection: BottomNavSection = BottomNavSection.FEED,
    swipedUserId: String? = null,
    swipedIsDislike: Boolean = false,
    blockedUserId: String? = null,
    modifier: Modifier = Modifier
) {
    val sessionStorage: SessionStorage = koinInject()
    val onboardingPreferences: OnboardingPreferences = koinInject()
    val uploadManager: PhotoUploadManager = koinInject()
    val snackbarHostState = remember { SnackbarHostState() }
    val datesViewModel: DatesViewModel = koinViewModel()
    val datesState by datesViewModel.state.collectAsStateWithLifecycle()
    val matchesViewModel: MatchesViewModel = koinViewModel()
    val matchesState by matchesViewModel.state.collectAsStateWithLifecycle()

    // Show in-app notifications when background photo uploads complete
    LaunchedEffect(Unit) {
        uploadManager.events.collect { event ->
            when (event) {
                is PhotoUploadEvent.Success -> {
                    val message = getString(Res.string.photo_upload_success, event.slotIndex + 1)
                    snackbarHostState.showSnackbar(message)
                }
                is PhotoUploadEvent.Failed -> {
                    val message = getString(Res.string.photo_upload_failed, event.slotIndex + 1)
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    val authInfo by sessionStorage.observeAuthInfo().collectAsStateWithLifecycle(null)
    val userStatus = authInfo?.user?.status
    var selectedSection by rememberSaveable { mutableStateOf(initialSection) }
    val datesCount = datesState.dates.size
    // Persisted across recompositions so the Dates tab doesn't disappear while the flow
    // reloads (e.g. after the app returns from background and WhileSubscribed resets state).
    // Only updated once loading completes to avoid flickering on the initial value.
    var hasAcceptedDates by rememberSaveable { mutableStateOf(false) }
    val visibleSections = remember(hasAcceptedDates) {
        if (hasAcceptedDates) BottomNavSection.entries.toList()
        else BottomNavSection.entries.filter { it != BottomNavSection.DATES }
    }
    // Track previous count to detect the transition 0 → 1 (first date accepted in-session).
    // -1 means "not yet initialized after initial load" — avoids false navigation on cold start.
    var prevDatesCount by rememberSaveable { mutableStateOf(-1) }
    LaunchedEffect(datesCount, datesState.isLoading) {
        // Wait until the initial fetch completes before tracking changes
        if (datesState.isLoading) return@LaunchedEffect

        // Once a date has been created, never hide the tab again
        if (datesCount > 0) hasAcceptedDates = true

        if (prevDatesCount == -1) {
            // Establish baseline after first load — do not navigate
            prevDatesCount = datesCount
            return@LaunchedEffect
        }

        if (datesCount == 1 && prevDatesCount == 0) {
            // First date just got accepted this session — jump to the Dates tab
            selectedSection = BottomNavSection.DATES
        }
        prevDatesCount = datesCount
    }

    // null = not yet loaded from DataStore, true/false = loaded value
    var hasSeenFeaturesOnboarding by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var hasSeenProfileSetup by rememberSaveable { mutableStateOf<Boolean?>(null) }
    // Track if features onboarding was just completed this session,
    // so we skip profile setup until the next app launch.
    var justCompletedFeaturesOnboarding by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasSeenFeaturesOnboarding = onboardingPreferences.hasSeenFeaturesOnboarding()
        hasSeenProfileSetup = onboardingPreferences.hasSeenProfileSetup()
    }

    // Persist flags to DataStore when they change to true
    LaunchedEffect(hasSeenFeaturesOnboarding) {
        if (hasSeenFeaturesOnboarding == true) {
            onboardingPreferences.markFeaturesOnboardingSeen()
        }
    }
    LaunchedEffect(hasSeenProfileSetup) {
        if (hasSeenProfileSetup == true) {
            onboardingPreferences.markProfileSetupSeen()
        }
    }

    // Emergency feature
    val emergencySettingsStorage: EmergencySettingsStorage = koinInject()
    val emergencySettings by emergencySettingsStorage.observe().collectAsStateWithLifecycle(null)
    val emergencyRepository: EmergencyContactRepository = koinInject()
    val emergencyContacts by emergencyRepository.getAll().collectAsStateWithLifecycle(emptyList())
    val emergencyViewModel: EmergencyContactsViewModel = koinViewModel()
    val emergencyState by emergencyViewModel.state.collectAsStateWithLifecycle()

    val isEmergencyEnabled = emergencySettings?.isEnabled == true
    val hasContacts = emergencyContacts.isNotEmpty()
    val showPanicButton = isEmergencyEnabled && hasContacts

    // Request SMS permission as soon as emergency feature is active
    val smsPermissionLauncher = rememberSmsPermissionLauncher { /* result handled by OS */ }
    LaunchedEffect(showPanicButton) {
        if (showPanicButton) smsPermissionLauncher.launch()
    }

    // Show snackbar feedback after SOS is sent
    LaunchedEffect(Unit) {
        emergencyViewModel.events.collect { event ->
            when (event) {
                is EmergencyContactsEvent.SosSent -> {
                    val message = getString(Res.string.sos_sent, event.contactCount)
                    snackbarHostState.showSnackbar(message)
                }
                else -> Unit
            }
        }
    }

    // Shake detector for SOS activation
    val shakeDetector: ShakeDetector = koinInject()
    DisposableEffect(showPanicButton) {
        if (showPanicButton) {
            shakeDetector.start {
                emergencyViewModel.startSosCountdown()
            }
        }
        onDispose { shakeDetector.stop() }
    }

    // Volume button x3 for SOS activation
    LaunchedEffect(showPanicButton) {
        if (showPanicButton) {
            VolumeButtonEventBus.events.collectLatest {
                emergencyViewModel.startSosCountdown()
            }
        }
    }

    // Show full-screen photo onboarding if user status is PENDING
    if (userStatus == UserStatus.PENDING) {
        PhotoOnboardingScreen(
            onComplete = {
                // No-op: the screen will automatically dismiss when session updates
                // with status ACTIVE, causing the check to pass
            },
            modifier = modifier
        )
        return
    }

    // Still loading session or preferences — avoid showing content briefly
    if (userStatus == null || hasSeenFeaturesOnboarding == null || hasSeenProfileSetup == null) return

    // Show features onboarding once (first launch after sign-up)
    if (hasSeenFeaturesOnboarding == false) {
        FeaturesOnboardingScreen(
            onComplete = {
                hasSeenFeaturesOnboarding = true
                justCompletedFeaturesOnboarding = true
            },
            modifier = modifier
        )
        return
    }

    // Show profile setup wizard on the next app launch (not in the same session as features onboarding)
    if (hasSeenProfileSetup == false && !justCompletedFeaturesOnboarding) {
        ProfileSetupFastScreen(
            onComplete = { hasSeenProfileSetup = true },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                selectedSection = selectedSection,
                sections = visibleSections,
                badges = buildMap {
                    val likesCount = matchesState.likes.size
                    if (likesCount > 0) put(BottomNavSection.MATCHES, likesCount)
                },
                onSectionSelected = { section ->
                    selectedSection = section
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            when (selectedSection) {
                BottomNavSection.FEED -> {
                    FeedRoot(
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToEditProfile = onEditProfile,
                        onNavigateToMatches = { selectedSection = BottomNavSection.MATCHES },
                        swipedUserId = swipedUserId,
                        swipedIsDislike = swipedIsDislike,
                        blockedUserId = blockedUserId
                    )
                }

                BottomNavSection.MATCHES -> {
                    MatchesRoot(
                        onNavigateToProfile = onNavigateToMatchProfile,
                        onNavigateToChatDetail = onNavigateToChatDetail
                    )
                }

                BottomNavSection.MESSAGES -> {
                    ChatListDetailAdaptiveLayout(
                        initialChatId = null,
                        onNavigateToChatDetail = onNavigateToChatDetail,
                        onNavigateToProfile = { userId ->
                            onNavigateToMatchProfile(userId, null)
                        }
                    )
                }

                BottomNavSection.DATES -> {
                    DatesRoot(
                        onNavigateToChatDetail = onNavigateToChatDetail
                    )
                }

                BottomNavSection.PROFILE -> {
                    ProfileScreen(
                        onEditProfile = onEditProfile,
                        onSettings = onSettings,
                        onVerification = onVerification,
                        onSubscriptions = onSubscriptions,
                        onSafetyCenter = onSafetyCenter,
                        onDateSafetyTips = onDateSafetyTips,
                        onDateSafetyChecklist = onDateSafetyChecklist,
                        onSafeDate = onSafeDate,
                        onNavigateToProfile = onNavigateToOwnProfile,
                        showSosButton = showPanicButton,
                        onSosTrigger = { emergencyViewModel.onAction(EmergencyContactsAction.OnSosTrigger) }
                    )
                }
            }

            // Panic Button overlay — only visible on the Messages tab
            if (showPanicButton && !emergencyState.showSosCountdown && selectedSection == BottomNavSection.MESSAGES) {
                PanicButton(
                    onTrigger = { emergencyViewModel.onAction(EmergencyContactsAction.OnSosTrigger) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }

    // SOS countdown dialog (shown above everything)
    if (emergencyState.showSosCountdown) {
        SosCountdownDialog(
            countdown = emergencyState.sosCountdown,
            onCancel = { emergencyViewModel.onAction(EmergencyContactsAction.OnSosCancel) }
        )
    }
}
