package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.UsageResponse
import java.util.*

interface AssistantUsageRepository {
    fun getUsage(userId: UUID, date: LocalDate): UsageResponse
    fun getRequestCount(userId: UUID, date: LocalDate): Int
    fun incrementUsage(userId: UUID, date: LocalDate, inputTokens: Int, outputTokens: Int)

    companion object {
        const val DAILY_REQUEST_LIMIT = 20
    }
}
