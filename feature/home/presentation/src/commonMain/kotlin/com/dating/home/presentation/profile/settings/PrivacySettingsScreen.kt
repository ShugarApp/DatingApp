package com.dating.home.presentation.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.settings_account_privacy
import aura.feature.home.presentation.generated.resources.settings_blocked_users
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit,
    onBlockedUsers: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.settings_account_privacy),
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AccessCardList(
                title = stringResource(Res.string.settings_account_privacy)
            ) {
                AccessCardItem(
                    icon = Icons.Default.Block,
                    title = stringResource(Res.string.settings_blocked_users),
                    onClick = onBlockedUsers
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
