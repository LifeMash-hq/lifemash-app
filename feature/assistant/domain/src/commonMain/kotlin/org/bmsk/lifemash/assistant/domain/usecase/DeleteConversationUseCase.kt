package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository

interface DeleteConversationUseCase {
    suspend operator fun invoke(id: String)
}

class DeleteConversationUseCaseImpl(
    private val repository: AssistantRepository,
) : DeleteConversationUseCase {
    override suspend operator fun invoke(id: String) =
        repository.deleteConversation(id)
}
