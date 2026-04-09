package com.dating.home.presentation.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.settings_guidelines
import aura.feature.home.presentation.generated.resources.settings_help
import aura.feature.home.presentation.generated.resources.settings_legal_account
import aura.feature.home.presentation.generated.resources.settings_privacy
import aura.feature.home.presentation.generated.resources.settings_terms
import com.dating.core.designsystem.components.cards.AccessCardItem
import com.dating.core.designsystem.components.cards.AccessCardList
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.presentation.util.rememberOpenUrl
import org.jetbrains.compose.resources.stringResource

private const val URL_HELP = "https://aura-safe-dating.com/#"
private const val URL_PRIVACY = "https://aura-safe-dating.com/#"
private const val URL_TERMS = "https://aura-safe-dating.com/#"
private const val URL_GUIDELINES = "https://aura-safe-dating.com/#"

@Composable
fun LegalAccountScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openUrl = rememberOpenUrl()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.settings_legal_account),
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
                title = stringResource(Res.string.settings_legal_account)
            ) {
                AccessCardItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = stringResource(Res.string.settings_help),
                    onClick = { openUrl(URL_HELP) }
                )
                AccessCardItem(
                    icon = Icons.Default.Gavel,
                    title = stringResource(Res.string.settings_guidelines),
                    onClick = { openUrl(URL_GUIDELINES) }
                )
                AccessCardItem(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(Res.string.settings_privacy),
                    onClick = { openUrl(URL_PRIVACY) }
                )
                AccessCardItem(
                    icon = Icons.Default.Description,
                    title = stringResource(Res.string.settings_terms),
                    onClick = { openUrl(URL_TERMS) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
