package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dating.core.domain.auth.SessionStorage
import com.dating.core.domain.auth.UserStatus
import com.dating.core.domain.preferences.OnboardingPreferences
import com.dating.home.presentation.chat.chat_list_detail.ChatListDetailAdaptiveLayout
import com.dating.home.presentation.features_onboarding.FeaturesOnboardingScreen
import com.dating.home.presentation.home.swipe.FeedRoot
import com.dating.home.presentation.matches.MatchesRoot
import com.dating.home.presentation.photo_onboarding.PhotoOnboardingScreen
import com.dating.home.presentation.profile.profile.ProfileScreen
import org.koin.compose.koinInject

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
    swipedUserId: String? = null,
    swipedIsDislike: Boolean = false,
    blockedUserId: String? = null,
    modifier: Modifier = Modifier
) {
    val sessionStorage: SessionStorage = koinInject()
    val onboardingPreferences: OnboardingPreferences = koinInject()

    val authInfo by sessionStorage.observeAuthInfo().collectAsStateWithLifecycle(null)
    val userStatus = authInfo?.user?.status
    var selectedSection by rememberSaveable { mutableStateOf(BottomNavSection.FEED) }

    // null = not yet loaded, true/false = loaded value
    var hasSeenFeaturesOnboarding by rememberSaveable { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        hasSeenFeaturesOnboarding = onboardingPreferences.hasSeenFeaturesOnboarding()
    }

    // Persist the "seen" flag to DataStore whenever the user completes the onboarding
    LaunchedEffect(hasSeenFeaturesOnboarding) {
        if (hasSeenFeaturesOnboarding == true) {
            onboardingPreferences.markFeaturesOnboardingSeen()
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
    if (userStatus == null || hasSeenFeaturesOnboarding == null) return

    // Show features onboarding once after account creation
    if (hasSeenFeaturesOnboarding == false) {
        FeaturesOnboardingScreen(
            onComplete = {
                hasSeenFeaturesOnboarding = true
            },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedSection = selectedSection,
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

                BottomNavSection.PROFILE -> {
                    ProfileScreen(
                        onEditProfile = onEditProfile,
                        onSettings = onSettings,
                        onVerification = onVerification,
                        onSubscriptions = onSubscriptions,
                        onNavigateToProfile = onNavigateToOwnProfile
                    )
                }
            }
        }
    }
}
