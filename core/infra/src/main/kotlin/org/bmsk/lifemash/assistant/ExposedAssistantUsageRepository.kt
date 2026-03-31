package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.UsageResponse
import org.bmsk.lifemash.db.tables.AssistantUsage
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedAssistantUsageRepository : AssistantUsageRepository {

    override fun getUsage(userId: Uuid, date: LocalDate): UsageResponse = transaction {
        val row = AssistantUsage.selectAll().where {
            (AssistantUsage.userId eq userId.toJavaUuid()) and (AssistantUsage.date eq date)
        }.singleOrNull()

        UsageResponse(
            date = date.toString(),
            inputTokens = row?.get(AssistantUsage.inputTokens) ?: 0,
            outputTokens = row?.get(AssistantUsage.outputTokens) ?: 0,
            requestCount = row?.get(AssistantUsage.requestCount) ?: 0,
            dailyLimit = DAILY_REQUEST_LIMIT,
        )
    }

    override fun getRequestCount(userId: Uuid, date: LocalDate): Int = transaction {
        AssistantUsage.selectAll().where {
            (AssistantUsage.userId eq userId.toJavaUuid()) and (AssistantUsage.date eq date)
        }.singleOrNull()?.get(AssistantUsage.requestCount) ?: 0
    }

    override fun incrementUsage(userId: Uuid, date: LocalDate, inputTokens: Int, outputTokens: Int) = transaction {
        val existing = AssistantUsage.selectAll().where {
            (AssistantUsage.userId eq userId.toJavaUuid()) and (AssistantUsage.date eq date)
        }.singleOrNull()

        if (existing == null) {
            AssistantUsage.insert {
                it[AssistantUsage.userId] = userId.toJavaUuid()
                it[AssistantUsage.date] = date
                it[AssistantUsage.inputTokens] = inputTokens
                it[AssistantUsage.outputTokens] = outputTokens
                it[requestCount] = 1
            }
        } else {
            AssistantUsage.update({
                (AssistantUsage.userId eq userId.toJavaUuid()) and (AssistantUsage.date eq date)
            }) {
                with(SqlExpressionBuilder) {
                    it.update(AssistantUsage.inputTokens, AssistantUsage.inputTokens + inputTokens)
                    it.update(AssistantUsage.outputTokens, AssistantUsage.outputTokens + outputTokens)
                    it.update(requestCount, requestCount + 1)
                }
            }
        }
        Unit
    }

    companion object {
        private const val DAILY_REQUEST_LIMIT = 20
    }
}
