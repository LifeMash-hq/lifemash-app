package org.bmsk.lifemash.user

import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.model.user.UserSettingsDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedUserRepository : UserRepository {

    override fun upsert(
        email: String,
        provider: String,
        providerId: String,
        nickname: String,
        profileImage: String?
    ): AuthUserDto = transaction {
        val existing = Users.selectAll().where {
            (Users.provider eq provider) and (Users.providerId eq providerId)
        }.singleOrNull()

        if (existing != null) {
            Users.update({ (Users.provider eq provider) and (Users.providerId eq providerId) }) {
                it[Users.nickname] = nickname
                it[Users.profileImage] = profileImage
                it[Users.updatedAt] = now()
            }
            existing.toDto()
        } else {
            val now = now()
            Users.insert {
                it[Users.email] = email
                it[Users.provider] = provider
                it[Users.providerId] = providerId
                it[Users.nickname] = nickname
                it[Users.profileImage] = profileImage
                it[Users.createdAt] = now
                it[Users.updatedAt] = now
            }.resultedValues!!.first().toDto()
        }
    }

    override fun findById(id: Uuid): AuthUserDto? = transaction {
        Users.selectAll().where { Users.id eq id.toJavaUuid() }.singleOrNull()?.toDto()
    }

    override fun findByEmail(email: String): AuthUserDto? = transaction {
        Users.selectAll().where { Users.email eq email }.singleOrNull()?.toDto()
    }

    override fun getPasswordHash(email: String): String? = transaction {
        Users.selectAll().where {
            (Users.email eq email) and (Users.provider eq "EMAIL")
        }.singleOrNull()?.get(Users.passwordHash)
    }

    override fun upsertEmailUser(email: String, passwordHash: String, nickname: String): AuthUserDto = transaction {
        val now = now()
        Users.insert {
            it[Users.email] = email
            it[Users.provider] = "EMAIL"
            it[Users.providerId] = email
            it[Users.nickname] = nickname
            it[Users.passwordHash] = passwordHash
            it[Users.createdAt] = now
            it[Users.updatedAt] = now
        }.resultedValues!!.first().toDto()
    }

    override fun getSettings(userId: Uuid): UserSettingsDto? = transaction {
        Users.selectAll().where { Users.id eq userId.toJavaUuid() }.singleOrNull()?.toSettingsDto()
    }

    override fun updateSettings(
        userId: Uuid,
        startScreen: String?,
        viewStyleSelf: String?,
        viewStyleOthers: String?,
        defaultVisibility: String?,
    ): UserSettingsDto? = transaction {
        val javaId = userId.toJavaUuid()
        Users.update({ Users.id eq javaId }) {
            if (startScreen != null) it[Users.startScreen] = startScreen
            if (viewStyleSelf != null) it[Users.viewStyleSelf] = viewStyleSelf
            if (viewStyleOthers != null) it[Users.viewStyleOthers] = viewStyleOthers
            if (defaultVisibility != null) it[Users.defaultVisibility] = defaultVisibility
            it[Users.updatedAt] = now()
        }
        Users.selectAll().where { Users.id eq javaId }.singleOrNull()?.toSettingsDto()
    }

    private fun ResultRow.toSettingsDto() = UserSettingsDto(
        startScreen = this[Users.startScreen],
        viewStyleSelf = this[Users.viewStyleSelf],
        viewStyleOthers = this[Users.viewStyleOthers],
        defaultVisibility = this[Users.defaultVisibility],
    )

    private fun ResultRow.toDto() = AuthUserDto(
        id = this[Users.id].toString(),
        email = this[Users.email],
        nickname = this[Users.nickname],
        profileImage = this[Users.profileImage],
        provider = this[Users.provider],
    )

    private fun now(): OffsetDateTime =
        nowUtc()
}
