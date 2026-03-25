package org.bmsk.lifemash.assistant.data.repository

import org.bmsk.lifemash.assistant.data.api.AssistantApi
import org.bmsk.lifemash.assistant.data.api.SseClient
import org.bmsk.lifemash.assistant.data.api.dto.toDomain
import org.bmsk.lifemash.assistant.data.api.dto.toRequest
import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.assistant.domain.model.SseEvent
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository
import org.bmsk.lifemash.model.assistant.ChatRequest

internal class AssistantRepositoryImpl(
    private val api: AssistantApi,
    private val sseClient: SseClient,
) : AssistantRepository {

    override suspend fun sendMessage(
        message: String,
        conversationId: String?,
        installedBlocks: List<InstalledBlock>,
        onEvent: suspend (SseEvent) -> Unit,
    ) {
        sseClient.streamChat(
            ChatRequest(
                message = message,
                conversationId = conversationId,
                installedBlocks = installedBlocks.map { it.toRequest() },
            ),
        ) { dto ->
            onEvent(dto.toDomain())
        }
    }

    override suspend fun getConversations(limit: Int, offset: Int): List<Conversation> =
        api.getConversations(limit, offset).map { it.toDomain() }

    override suspend fun getConversation(id: String): Pair<Conversation, List<ChatMessage>> {
        val detail = api.getConversation(id)
        val conversation = Conversation(
            id = detail.id,
            title = detail.title,
            createdAt = detail.createdAt,
            updatedAt = detail.createdAt,
        )
        val messages = detail.messages.map { it.toDomain() }
        return conversation to messages
    }

    override suspend fun deleteConversation(id: String) =
        api.deleteConversation(id)

    override suspend fun getUsage(date: String?): AssistantUsage =
        api.getUsage(date).toDomain()
}
