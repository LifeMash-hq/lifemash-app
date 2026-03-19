package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.model.SseEvent
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository

interface SendMessageUseCase {
    suspend operator fun invoke(message: String, conversationId: String?, onEvent: suspend (SseEvent) -> Unit)
}

class SendMessageUseCaseImpl(
    private val repository: AssistantRepository,
) : SendMessageUseCase {
    override suspend operator fun invoke(
        message: String,
        conversationId: String?,
        onEvent: suspend (SseEvent) -> Unit,
    ) = repository.sendMessage(message, conversationId, onEvent)
}
