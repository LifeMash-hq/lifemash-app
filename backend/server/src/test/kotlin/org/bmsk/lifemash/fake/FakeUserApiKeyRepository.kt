package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import org.bmsk.lifemash.assistant.UserApiKeyRepository
import java.util.*

class FakeUserApiKeyRepository : UserApiKeyRepository {
    private val keys = mutableMapOf<UUID, String>()

    override fun saveApiKey(userId: UUID, apiKey: String) {
        keys[userId] = apiKey
    }

    override fun getDecryptedApiKey(userId: UUID): String? = keys[userId]

    override fun deleteApiKey(userId: UUID): Boolean = keys.remove(userId) != null

    override fun hasApiKey(userId: UUID): ApiKeyStatusResponse =
        ApiKeyStatusResponse(hasKey = keys.containsKey(userId), provider = if (keys.containsKey(userId)) "claude" else null)
}
