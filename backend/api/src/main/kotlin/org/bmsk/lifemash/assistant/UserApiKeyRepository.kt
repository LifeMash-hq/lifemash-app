package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import java.util.*

interface UserApiKeyRepository {
    fun saveApiKey(userId: UUID, apiKey: String)
    fun getDecryptedApiKey(userId: UUID): String?
    fun deleteApiKey(userId: UUID): Boolean
    fun hasApiKey(userId: UUID): ApiKeyStatusResponse
}
