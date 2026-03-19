package org.bmsk.lifemash.assistant.domain.usecase

import org.bmsk.lifemash.assistant.domain.repository.ApiKeyRepository

interface SaveApiKeyUseCase {
    suspend operator fun invoke(key: String)
}

class SaveApiKeyUseCaseImpl(
    private val repository: ApiKeyRepository,
) : SaveApiKeyUseCase {
    override suspend operator fun invoke(key: String) =
        repository.saveApiKey(key)
}
