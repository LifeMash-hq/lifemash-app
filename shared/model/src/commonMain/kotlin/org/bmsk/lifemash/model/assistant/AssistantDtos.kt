package org.bmsk.lifemash.model.assistant

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String,
    val conversationId: String? = null,
    val installedBlocks: List<InstalledBlockContext> = emptyList(),
)

@Serializable
data class InstalledBlockContext(val id: String, val url: String)

@Serializable
data class SseEvent(
    val type: String,
    val content: String? = null,
    val tool: String? = null,
    val conversationId: String? = null,
    val usage: UsageInfo? = null,
)

@Serializable
data class UsageInfo(val inputTokens: Int, val outputTokens: Int)

@Serializable
data class ConversationDto(
    val id: String,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
data class ConversationDetailDto(
    val id: String,
    val title: String,
    val messages: List<MessageDto>,
    val createdAt: Instant,
)

@Serializable
data class MessageDto(
    val id: String,
    val role: String,
    val content: String,
    val createdAt: Instant,
)

@Serializable
data class SaveApiKeyRequest(val apiKey: String)

@Serializable
data class ApiKeyStatusResponse(
    val hasKey: Boolean,
    val provider: String? = null,
)

@Serializable
data class UsageResponse(
    val date: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val requestCount: Int,
    val dailyLimit: Int,
)
