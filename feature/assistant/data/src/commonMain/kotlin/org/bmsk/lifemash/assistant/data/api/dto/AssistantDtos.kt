package org.bmsk.lifemash.assistant.data.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.SseEvent

@Serializable
internal data class ChatRequestDto(
    val message: String,
    val conversationId: String? = null,
)

@Serializable
internal data class SseEventDto(
    val type: String,
    val content: String? = null,
    val tool: String? = null,
    val conversationId: String? = null,
    val usage: UsageInfoDto? = null,
) {
    fun toDomain() = SseEvent(
        type = type,
        content = content,
        tool = tool,
        conversationId = conversationId,
        inputTokens = usage?.inputTokens,
        outputTokens = usage?.outputTokens,
    )
}

@Serializable
internal data class UsageInfoDto(
    val inputTokens: Int,
    val outputTokens: Int,
)

@Serializable
internal data class ConversationDto(
    val id: String,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun toDomain() = Conversation(
        id = id,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

@Serializable
internal data class ConversationDetailDto(
    val id: String,
    val title: String,
    val messages: List<MessageDto>,
    val createdAt: Instant,
)

@Serializable
internal data class MessageDto(
    val id: String,
    val role: String,
    val content: String,
    val createdAt: Instant,
) {
    fun toDomain() = ChatMessage(
        id = id,
        role = role,
        content = content,
        createdAt = createdAt,
    )
}

@Serializable
internal data class SaveApiKeyRequestDto(val apiKey: String)

@Serializable
internal data class ApiKeyStatusResponseDto(
    val hasKey: Boolean,
    val provider: String? = null,
)

@Serializable
internal data class UsageResponseDto(
    val date: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val requestCount: Int,
    val dailyLimit: Int,
) {
    fun toDomain() = AssistantUsage(
        date = date,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        requestCount = requestCount,
        dailyLimit = dailyLimit,
    )
}
