package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import java.util.*

class StubUserApiKeyRepository : UserApiKeyRepository {
    override fun saveApiKey(userId: UUID, apiKey: String) {}

    override fun getDecryptedApiKey(userId: UUID): String? = null

    override fun deleteApiKey(userId: UUID): Boolean = true

    override fun hasApiKey(userId: UUID): ApiKeyStatusResponse =
        ApiKeyStatusResponse(hasKey = false)
}
