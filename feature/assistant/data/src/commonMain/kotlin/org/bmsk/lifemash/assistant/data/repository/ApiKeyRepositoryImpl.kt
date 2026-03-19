package org.bmsk.lifemash.assistant.data.repository

import org.bmsk.lifemash.assistant.data.api.AssistantApi
import org.bmsk.lifemash.assistant.domain.repository.ApiKeyRepository

internal class ApiKeyRepositoryImpl(
    private val api: AssistantApi,
) : ApiKeyRepository {

    override suspend fun saveApiKey(key: String) =
        api.saveApiKey(key)

    override suspend fun removeApiKey() =
        api.deleteApiKey()

    override suspend fun getApiKeyStatus(): Boolean =
        api.getApiKeyStatus().hasKey
}
