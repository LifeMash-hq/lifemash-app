package org.bmsk.lifemash.assistant

import org.bmsk.lifemash.model.assistant.ApiKeyStatusResponse
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.db.tables.UserApiKeys
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ExposedUserApiKeyRepository : UserApiKeyRepository {

    override fun saveApiKey(userId: UUID, apiKey: String) = transaction {
        val encrypted = ApiKeyEncryption.encrypt(apiKey)
        val now = now()

        val existing = UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId }
            .singleOrNull()

        if (existing == null) {
            UserApiKeys.insert {
                it[UserApiKeys.userId] = userId
                it[encryptedKey] = encrypted
                it[provider] = "claude"
                it[createdAt] = now
                it[updatedAt] = now
            }
        } else {
            UserApiKeys.update({ UserApiKeys.userId eq userId }) {
                it[encryptedKey] = encrypted
                it[provider] = "claude"
                it[updatedAt] = now
            }
        }
        Unit
    }

    override fun getDecryptedApiKey(userId: UUID): String? = transaction {
        UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId }
            .singleOrNull()
            ?.let { ApiKeyEncryption.decrypt(it[UserApiKeys.encryptedKey]) }
    }

    override fun deleteApiKey(userId: UUID): Boolean = transaction {
        UserApiKeys.deleteWhere { UserApiKeys.userId eq userId } > 0
    }

    override fun hasApiKey(userId: UUID): ApiKeyStatusResponse = transaction {
        val row = UserApiKeys.selectAll()
            .where { UserApiKeys.userId eq userId }
            .singleOrNull()

        ApiKeyStatusResponse(
            hasKey = row != null,
            provider = row?.get(UserApiKeys.provider),
        )
    }

    private fun now(): OffsetDateTime =
        nowUtc()
}
