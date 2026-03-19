package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.repository.ApiKeyRepository

interface RemoveApiKeyUseCase {
    suspend operator fun invoke()
}

class RemoveApiKeyUseCaseImpl(
    private val repository: ApiKeyRepository,
) : RemoveApiKeyUseCase {
    override suspend operator fun invoke() =
        repository.removeApiKey()
}
