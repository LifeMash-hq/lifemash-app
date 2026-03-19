package org.bmsk.lifemash.assistant.domain.model

data class SseEvent(
    val type: String,
    val content: String? = null,
    val tool: String? = null,
    val conversationId: String? = null,
    val inputTokens: Int? = null,
    val outputTokens: Int? = null,
)
