package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.model.AssistantUsage
import org.bmsk.lifemash.assistant.domain.repository.AssistantRepository

interface GetUsageUseCase {
    suspend operator fun invoke(date: String? = null): AssistantUsage
}

class GetUsageUseCaseImpl(
    private val repository: AssistantRepository,
) : GetUsageUseCase {
    override suspend operator fun invoke(date: String?): AssistantUsage =
        repository.getUsage(date)
}
