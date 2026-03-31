package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.db.tables.UserApiKeys
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedUserApiKeyRepository : UserApiKeyRepository {

    override fun saveApiKey(userId: Uuid, apiKey: String) = transaction {
        val encrypted = ApiKeyEncryption.encrypt(apiKey)
        val now = now()

        val existing = UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId.toJavaUuid() }
            .singleOrNull()

        if (existing == null) {
            UserApiKeys.insert {
                it[UserApiKeys.userId] = userId.toJavaUuid()
                it[encryptedKey] = encrypted
                it[provider] = "claude"
                it[createdAt] = now
                it[updatedAt] = now
            }
        } else {
            UserApiKeys.update({ UserApiKeys.userId eq userId.toJavaUuid() }) {
                it[encryptedKey] = encrypted
                it[provider] = "claude"
                it[updatedAt] = now
            }
        }
        Unit
    }

    override fun getDecryptedApiKey(userId: Uuid): String? = transaction {
        UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId.toJavaUuid() }
            .singleOrNull()
            ?.let { ApiKeyEncryption.decrypt(it[UserApiKeys.encryptedKey]) }
    }

    override fun deleteApiKey(userId: Uuid): Boolean = transaction {
        UserApiKeys.deleteWhere { UserApiKeys.userId eq userId.toJavaUuid() } > 0
    }

    override fun hasApiKey(userId: Uuid): ApiKeyStatusResponse = transaction {
        val row = UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId.toJavaUuid() }
            .singleOrNull()

        ApiKeyStatusResponse(
            hasKey = row != null,
            provider = row?.get(UserApiKeys.provider),
        )
    }

    private fun now(): OffsetDateTime =
        nowUtc()
}
