package com.dating.chat.presentation.bottom_navigation

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
import com.dating.chat.presentation.chat_list_detail.ChatListDetailAdaptiveLayout
import com.dating.chat.presentation.feed.FeedRoot
import com.dating.chat.presentation.matches.MatchesRoot
import com.dating.chat.presentation.profile.profile_hub.ProfileScreen

@Composable
fun BottomNavigationContainer(
    onLogout: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onVerification: () -> Unit,
    onSubscriptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by rememberSaveable {
        mutableStateOf(BottomNavSection.FEED)
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
                .padding(paddingValues)
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
                    )
                }

                BottomNavSection.PROFILE -> {
                    ProfileScreen(
                        onLogout = onLogout,
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
