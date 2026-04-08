package com.dating.home.presentation.dates

import com.dating.home.domain.models.AcceptedDateProposal
import com.dating.home.domain.models.DateProposalStatus

enum class DateFilter(
    val label: String,
    val statuses: Set<DateProposalStatus>?,
    val chipStatus: DateProposalStatus?
) {
    UPCOMING("Upcoming", setOf(DateProposalStatus.PENDING, DateProposalStatus.ACCEPTED), null),
    PENDING("Pending", setOf(DateProposalStatus.PENDING), DateProposalStatus.PENDING),
    ACCEPTED("Accepted", setOf(DateProposalStatus.ACCEPTED), DateProposalStatus.ACCEPTED),
    CANCELLED("Cancelled", setOf(DateProposalStatus.CANCELLED), DateProposalStatus.CANCELLED),
    REJECTED("Declined", setOf(DateProposalStatus.REJECTED), DateProposalStatus.REJECTED)
}

data class DatesState(
    val dates: List<AcceptedDateProposal> = emptyList(),
    val isLoading: Boolean = true,
    val selectedFilter: DateFilter = DateFilter.UPCOMING
) {
    val filteredDates: List<AcceptedDateProposal>
        get() = if (selectedFilter.statuses == null) dates
                else dates.filter { it.status in selectedFilter.statuses }

    fun countFor(filter: DateFilter): Int =
        if (filter.statuses == null) dates.size
        else dates.count { it.status in filter.statuses }
}
