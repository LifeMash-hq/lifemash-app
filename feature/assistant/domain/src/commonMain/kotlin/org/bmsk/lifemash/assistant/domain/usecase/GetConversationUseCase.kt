package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.model.ChatMessage
import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository

interface GetConversationUseCase {
    suspend operator fun invoke(id: String): Pair<Conversation, List<ChatMessage>>
}

class GetConversationUseCaseImpl(
    private val repository: AssistantRepository,
) : GetConversationUseCase {
    override suspend operator fun invoke(id: String): Pair<Conversation, List<ChatMessage>> =
        repository.getConversation(id)
}
