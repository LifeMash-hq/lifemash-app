package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import kotlin.uuid.Uuid

interface UserApiKeyRepository {
    fun saveApiKey(userId: Uuid, apiKey: String)
    fun getDecryptedApiKey(userId: Uuid): String?
    fun deleteApiKey(userId: Uuid): Boolean
    fun hasApiKey(userId: Uuid): ApiKeyStatusResponse
}
