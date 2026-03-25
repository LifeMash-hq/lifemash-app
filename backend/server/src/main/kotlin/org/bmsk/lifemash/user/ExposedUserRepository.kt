package org.bmsk.lifemash.user

import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.db.tables.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class ExposedUserRepository : UserRepository {

    override fun upsert(email: String, provider: String, providerId: String, nickname: String, profileImage: String?): AuthUserDto =
        transaction {
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

    override fun findById(id: UUID): AuthUserDto? = transaction {
        Users.selectAll().where { Users.id eq id }.singleOrNull()?.toDto()
    }

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
