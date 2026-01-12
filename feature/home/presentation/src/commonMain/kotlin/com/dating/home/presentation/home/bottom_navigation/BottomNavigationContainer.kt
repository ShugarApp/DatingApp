package com.dating.home.presentation.home.bottom_navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dating.home.presentation.chat.chat_list_detail.ChatListDetailAdaptiveLayout
import com.dating.home.presentation.home.swipe.FeedRoot
import com.dating.home.presentation.matches.MatchesRoot
import com.dating.home.presentation.profile.profile.ProfileScreen

@Composable
fun BottomNavigationContainer(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by rememberSaveable { mutableStateOf(BottomNavSection.FEED) }

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
                    FeedRoot(onNavigateToProfile = onNavigateToProfile)
                }

                BottomNavSection.MATCHES -> {
                    MatchesRoot()
                }

                BottomNavSection.MESSAGES -> {
                    ChatListDetailAdaptiveLayout(
                        initialChatId = null,
                        onNavigateToChatDetail = onNavigateToChatDetail
                    )
                }

                BottomNavSection.PROFILE -> {
                    ProfileScreen(
                        onEditProfile = onEditProfile,
                        onSettings = onSettings,
                        onVerification = onVerification,
                        onSubscriptions = onSubscriptions
                    )
                }
            }
        }
    }
}
