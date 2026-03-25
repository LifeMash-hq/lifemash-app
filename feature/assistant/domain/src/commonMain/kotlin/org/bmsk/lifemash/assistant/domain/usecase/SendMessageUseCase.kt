package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.model.InstalledBlock
import org.bmsk.lifemash.assistant.domain.model.SseEvent
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository
import org.bmsk.lifemash.validation.ChatMessageContent

interface SendMessageUseCase {
    suspend operator fun invoke(
        message: String,
        conversationId: String?,
        installedBlocks: List<InstalledBlock>,
        onEvent: suspend (SseEvent) -> Unit,
    )
}

class SendMessageUseCaseImpl(
    private val repository: AssistantRepository,
) : SendMessageUseCase {
    override suspend operator fun invoke(
        message: String,
        conversationId: String?,
        installedBlocks: List<InstalledBlock>,
        onEvent: suspend (SseEvent) -> Unit,
    ) {
        ChatMessageContent.of(message)
        repository.sendMessage(message, conversationId, installedBlocks, onEvent)
    }
}
