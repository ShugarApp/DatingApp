package com.dating.home.presentation.dates

import com.dating.home.domain.models.AcceptedDateProposal
import com.dating.home.domain.models.DateProposalStatus

enum class DateFilter(val label: String, val status: DateProposalStatus?) {
    ALL("All", null),
    PENDING("Pending", DateProposalStatus.PENDING),
    ACCEPTED("Accepted", DateProposalStatus.ACCEPTED),
    CANCELLED("Cancelled", DateProposalStatus.CANCELLED),
    REJECTED("Rejected", DateProposalStatus.REJECTED)
}

data class DatesState(
    val dates: List<AcceptedDateProposal> = emptyList(),
    val isLoading: Boolean = true,
    val selectedFilter: DateFilter = DateFilter.ALL
) {
    val filteredDates: List<AcceptedDateProposal>
        get() = if (selectedFilter.status == null) dates
                else dates.filter { it.status == selectedFilter.status }

    fun countFor(filter: DateFilter): Int =
        if (filter.status == null) dates.size
        else dates.count { it.status == filter.status }
}
