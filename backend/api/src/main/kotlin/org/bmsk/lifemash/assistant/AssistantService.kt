package org.bmsk.lifemash.assistant

import kotlinx.datetime.LocalDate
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.ConversationDetailDto
import org.bmsk.lifemash.model.assistant.ConversationDto
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.model.assistant.UsageResponse

interface AssistantService {
    suspend fun chat(userId: String, request: ChatRequest, emitEvent: suspend (SseEvent) -> Unit)
    fun getConversations(userId: String, limit: Int, offset: Long): List<ConversationDto>
    fun getConversationDetail(userId: String, conversationId: String): ConversationDetailDto
    fun deleteConversation(userId: String, conversationId: String)
    fun getUsage(userId: String, date: LocalDate?): UsageResponse
}
