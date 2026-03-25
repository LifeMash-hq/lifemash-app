package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.UsageResponse
import java.util.*

class StubAssistantUsageRepository : AssistantUsageRepository {
    override fun getUsage(userId: UUID, date: LocalDate): UsageResponse =
        UsageResponse(
            date = date.toString(),
            inputTokens = 0,
            outputTokens = 0,
            requestCount = 0,
            dailyLimit = AssistantUsageRepository.DAILY_REQUEST_LIMIT,
        )

    override fun getRequestCount(userId: UUID, date: LocalDate): Int = 0

    override fun incrementUsage(userId: UUID, date: LocalDate, inputTokens: Int, outputTokens: Int) {}
}
