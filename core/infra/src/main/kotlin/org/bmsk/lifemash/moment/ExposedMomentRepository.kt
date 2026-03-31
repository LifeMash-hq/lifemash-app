package org.bmsk.lifemash.moment

import org.bmsk.lifemash.db.tables.Moments
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedMomentRepository : MomentRepository {

    override fun create(
        eventId: Uuid,
        authorId: Uuid,
        imageUrl: String,
        caption: String?,
        visibility: String,
    ): MomentDto = transaction {
        val momentId = Moments.insert {
            it[Moments.eventId] = eventId.toJavaUuid()
            it[Moments.authorId] = authorId.toJavaUuid()
            it[Moments.imageUrl] = imageUrl
            it[Moments.caption] = caption
            it[Moments.visibility] = visibility
            it[Moments.createdAt] = nowUtc()
        }[Moments.id]

        momentWithAuthor()
            .where { Moments.id eq momentId }
            .single()
            .toDto()
    }

    override fun findById(momentId: Uuid): MomentDto? = transaction {
        momentWithAuthor()
            .where { Moments.id eq momentId.toJavaUuid() }
            .singleOrNull()
            ?.toDto()
    }

    override fun findByUser(userId: Uuid, viewerId: Uuid?): List<MomentDto> = transaction {
        val javaUserId = userId.toJavaUuid()
        val isSelf = viewerId != null && userId == viewerId

        momentWithAuthor()
            .where {
                val base = Moments.authorId eq javaUserId
                if (isSelf) base else base and (Moments.visibility eq "public")
            }
            .orderBy(Moments.createdAt, SortOrder.DESC)
            .map { it.toDto() }
    }

    override fun delete(momentId: Uuid): Unit = transaction {
        Moments.deleteWhere { Moments.id eq momentId.toJavaUuid() }
    }

    // ── private helpers ──

    /** Moments JOIN Users — author 정보 포함 쿼리 기반 */
    private fun momentWithAuthor(): Query =
        Moments.join(Users, JoinType.INNER, Moments.authorId, Users.id).selectAll()

    /** 순수 매핑 — ResultRow → MomentDto */
    private fun ResultRow.toDto() = MomentDto(
        id = this[Moments.id].toString(),
        eventId = this[Moments.eventId].toString(),
        authorId = this[Moments.authorId].toString(),
        authorNickname = this[Users.nickname],
        authorProfileImage = this[Users.profileImage],
        imageUrl = this[Moments.imageUrl],
        caption = this[Moments.caption],
        visibility = this[Moments.visibility],
        createdAt = this[Moments.createdAt].toString(),
    )
}
