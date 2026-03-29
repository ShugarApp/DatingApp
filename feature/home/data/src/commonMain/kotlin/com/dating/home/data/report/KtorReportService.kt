package com.dating.home.data.report

import com.dating.core.data.networking.post
import com.dating.core.domain.util.DataError
import com.dating.core.domain.util.Result
import com.dating.core.domain.util.map
import com.dating.home.domain.report.ReportReason
import com.dating.home.domain.report.ReportResult
import com.dating.home.domain.report.ReportService
import io.ktor.client.HttpClient

class KtorReportService(private val httpClient: HttpClient) : ReportService {

    override suspend fun reportUser(
        userId: String,
        reason: ReportReason,
        description: String?
    ): Result<ReportResult, DataError.Remote> {
        return httpClient.post<ReportRequestDto, ReportResponseDto>(
            route = "/users/$userId/report",
            body = ReportRequestDto(
                reason = reason.name,
                description = description?.takeIf { it.isNotBlank() }
            )
        ).map { dto ->
            ReportResult(id = dto.id, message = dto.message)
        }
    }
}
