package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.repository.ApiKeyRepository

interface GetApiKeyStatusUseCase {
    suspend operator fun invoke(): Boolean
}

class GetApiKeyStatusUseCaseImpl(
    private val repository: ApiKeyRepository,
) : GetApiKeyStatusUseCase {
    override suspend operator fun invoke(): Boolean =
        repository.getApiKeyStatus()
}
