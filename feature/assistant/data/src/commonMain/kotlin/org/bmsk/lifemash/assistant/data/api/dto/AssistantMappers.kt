package org.bmsk.lifemash.assistant.data.api.dto

import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.InstalledBlockContext
import org.bmsk.lifemash.model.assistant.MessageDto
import org.bmsk.lifemash.model.assistant.UsageResponse
import org.bmsk.lifemash.assistant.domain.model.SseEvent as DomainSseEvent

fun org.bmsk.lifemash.model.assistant.SseEvent.toDomain() = DomainSseEvent(
    type = type,
    content = content,
    tool = tool,
    conversationId = conversationId,
    inputTokens = usage?.inputTokens,
    outputTokens = usage?.outputTokens,
)

fun ConversationDto.toDomain() = Conversation(
    id = id, title = title, createdAt = createdAt, updatedAt = updatedAt,
)

fun MessageDto.toDomain() = ChatMessage(
    id = id, role = role, content = content, createdAt = createdAt,
)

fun UsageResponse.toDomain() = AssistantUsage(
    date = date, inputTokens = inputTokens, outputTokens = outputTokens,
    requestCount = requestCount, dailyLimit = dailyLimit,
)

fun InstalledBlock.toRequest() = InstalledBlockContext(id = id, url = url)
