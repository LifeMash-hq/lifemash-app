package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.model.Conversation
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository

interface GetConversationsUseCase {
    suspend operator fun invoke(limit: Int = 20, offset: Int = 0): List<Conversation>
}

class GetConversationsUseCaseImpl(
    private val repository: AssistantRepository,
) : GetConversationsUseCase {
    override suspend operator fun invoke(limit: Int, offset: Int): List<Conversation> =
        repository.getConversations(limit, offset)
}
