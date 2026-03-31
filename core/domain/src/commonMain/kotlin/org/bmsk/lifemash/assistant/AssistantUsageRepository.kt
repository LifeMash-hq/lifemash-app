package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.UsageResponse
import kotlin.uuid.Uuid

interface AssistantUsageRepository {
    fun getUsage(userId: Uuid, date: LocalDate): UsageResponse
    fun getRequestCount(userId: Uuid, date: LocalDate): Int
    fun incrementUsage(userId: Uuid, date: LocalDate, inputTokens: Int, outputTokens: Int)
}
