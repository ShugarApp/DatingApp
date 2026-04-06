package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dating.home.domain.models.DateProposalLocation

@Composable
expect fun PlatformLocationMap(
    onLocationChanged: (DateProposalLocation) -> Unit,
    onMovingChanged: (Boolean) -> Unit,
    modifier: Modifier
)
