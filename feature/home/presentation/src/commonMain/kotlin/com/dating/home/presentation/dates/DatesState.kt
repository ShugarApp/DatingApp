package com.dating.home.presentation.dates

import com.dating.home.domain.models.AcceptedDateProposal

data class DatesState(
    val dates: List<AcceptedDateProposal> = emptyList(),
    val isLoading: Boolean = true
)
