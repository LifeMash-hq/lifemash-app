package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.model.assistant.UsageResponse
import kotlinx.datetime.Instant

class StubAssistantService : AssistantService {
    private val epoch = Instant.fromEpochSeconds(0)

    override suspend fun chat(userId: String, request: ChatRequest, emitEvent: suspend (SseEvent) -> Unit) {
        emitEvent(SseEvent(type = "text", content = "데모 모드입니다. 실제 서비스에서는 AI 어시스턴트가 응답합니다."))
    }

    override fun getConversations(userId: String, limit: Int, offset: Long): List<ConversationDto> =
        emptyList()

    override fun getConversationDetail(userId: String, conversationId: String): ConversationDetailDto =
        ConversationDetailDto(
            id = conversationId,
            title = "Demo Conversation",
            messages = emptyList(),
            createdAt = epoch,
        )

    override fun deleteConversation(userId: String, conversationId: String) {}

    override fun getUsage(userId: String, date: LocalDate?): UsageResponse =
        UsageResponse(
            date = date?.toString() ?: "2024-01-01",
            inputTokens = 0,
            outputTokens = 0,
            requestCount = 0,
            dailyLimit = 20,
        )
}
