package org.bmsk.lifemash.assistant.domain.repository

interface ApiKeyRepository {
    suspend fun saveApiKey(key: String)
    suspend fun removeApiKey()
    suspend fun getApiKeyStatus(): Boolean
}
