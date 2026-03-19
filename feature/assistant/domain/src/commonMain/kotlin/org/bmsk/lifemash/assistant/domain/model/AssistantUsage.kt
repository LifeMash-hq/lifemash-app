package org.bmsk.lifemash.assistant.domain.model

data class AssistantUsage(
    val date: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val requestCount: Int,
    val dailyLimit: Int,
)
