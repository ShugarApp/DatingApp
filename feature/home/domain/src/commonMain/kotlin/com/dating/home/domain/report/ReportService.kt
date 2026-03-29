package com.dating.home.domain.report

import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result

interface ReportService {
    suspend fun reportUser(userId: String, reason: ReportReason, description: String?): Result<ReportResult, DataError.Remote>
}

enum class ReportReason {
    HARASSMENT,
    FAKE_PROFILE,
    INAPPROPRIATE_CONTENT,
    UNDERAGE,
    SPAM,
    SCAM,
    OTHER
}

data class ReportResult(
    val id: String,
    val message: String
)
