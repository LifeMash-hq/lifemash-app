package org.bmsk.lifemash.fake

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.assistant.AssistantUsageRepository
import org.bmsk.lifemash.model.assistant.UsageResponse
import java.util.*

class FakeAssistantUsageRepository : AssistantUsageRepository {
    data class UsageData(var inputTokens: Int = 0, var outputTokens: Int = 0, var requestCount: Int = 0)

    private val data = mutableMapOf<Pair<UUID, LocalDate>, UsageData>()

    override fun getUsage(userId: UUID, date: LocalDate): UsageResponse {
        val d = data[userId to date]
        return UsageResponse(
            date = date.toString(),
            inputTokens = d?.inputTokens ?: 0,
            outputTokens = d?.outputTokens ?: 0,
            requestCount = d?.requestCount ?: 0,
            dailyLimit = AssistantUsageRepository.DAILY_REQUEST_LIMIT,
        )
    }

    override fun getRequestCount(userId: UUID, date: LocalDate): Int =
        data[userId to date]?.requestCount ?: 0

    override fun incrementUsage(userId: UUID, date: LocalDate, inputTokens: Int, outputTokens: Int) {
        val d = data.getOrPut(userId to date) { UsageData() }
        d.inputTokens += inputTokens
        d.outputTokens += outputTokens
        d.requestCount++
    }

    fun setRequestCount(userId: UUID, date: LocalDate, count: Int) {
        val d = data.getOrPut(userId to date) { UsageData() }
        d.requestCount = count
    }
}
