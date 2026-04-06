package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.runtime.Composable
import com.dating.home.domain.models.DateProposalLocation

@Composable
expect fun PlatformLocationPicker(
    initialLocation: DateProposalLocation?,
    onLocationSelected: (DateProposalLocation) -> Unit,
    onDismiss: () -> Unit
)
