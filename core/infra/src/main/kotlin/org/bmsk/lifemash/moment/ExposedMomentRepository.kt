package org.bmsk.lifemash.moment

import org.bmsk.lifemash.db.tables.Events
import org.bmsk.lifemash.db.tables.MomentMedia
import org.bmsk.lifemash.db.tables.Moments
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MediaItemDto
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedMomentRepository : MomentRepository {

    override fun create(authorId: Uuid, request: CreateMomentRequest): MomentDto = transaction {
        val momentId = Moments.insert {
            it[Moments.eventId] = request.eventId?.let { id -> Uuid.parse(id).toJavaUuid() }
            it[Moments.authorId] = authorId.toJavaUuid()
            it[Moments.caption] = request.caption
            it[Moments.visibility] = request.visibility
            it[Moments.createdAt] = nowUtc()
        }[Moments.id]

        if (request.media.isNotEmpty()) {
            MomentMedia.batchInsert(request.media) { item ->
                this[MomentMedia.momentId] = momentId
                this[MomentMedia.mediaUrl] = item.mediaUrl
                this[MomentMedia.mediaType] = item.mediaType
                this[MomentMedia.sortOrder] = item.sortOrder
                this[MomentMedia.width] = item.width
                this[MomentMedia.height] = item.height
                this[MomentMedia.durationMs] = item.durationMs
            }
        }

        val media = mediaForMoment(momentId)
        momentWithAuthor()
            .where { Moments.id eq momentId }
            .single()
            .toDto(media)
    }

    override fun findById(momentId: Uuid): MomentDto? = transaction {
        val javaId = momentId.toJavaUuid()
        val row = momentWithAuthor()
            .where { Moments.id eq javaId }
            .singleOrNull() ?: return@transaction null
        val media = mediaForMoment(javaId)
        row.toDto(media)
    }

    override fun findByUser(userId: Uuid, viewerId: Uuid?): List<MomentDto> = transaction {
        val javaUserId = userId.toJavaUuid()
        val isSelf = viewerId != null && userId == viewerId

        val rows = momentWithAuthor()
            .where {
                val base = Moments.authorId eq javaUserId
                if (isSelf) base
                else if (viewerId == null) base and (Moments.visibility eq "public")
                else base and ((Moments.visibility eq "public") or (Moments.visibility eq "followers"))
            }
            .orderBy(Moments.createdAt, SortOrder.DESC)
            .toList()

        val momentIds = rows.map { it[Moments.id] }
        val mediaByMoment = if (momentIds.isEmpty()) emptyMap()
        else MomentMedia.selectAll()
            .where { MomentMedia.momentId inList momentIds }
            .orderBy(MomentMedia.sortOrder, SortOrder.ASC)
            .groupBy { it[MomentMedia.momentId] }
            .mapValues { (_, mediaRows) -> mediaRows.map { it.toMediaDto() } }

        rows.map { it.toDto(mediaByMoment[it[Moments.id]] ?: emptyList()) }
    }

    override fun delete(momentId: Uuid): Unit = transaction {
        // moment_media는 ON DELETE CASCADE로 자동 삭제됨
        Moments.deleteWhere { Moments.id eq momentId.toJavaUuid() }
    }

    // ── private helpers ──

    private fun momentWithAuthor() =
        Moments
            .join(Users, JoinType.INNER, Moments.authorId, Users.id)
            .join(Events, JoinType.LEFT) { Moments.eventId eq Events.id }
            .selectAll()

    private fun mediaForMoment(momentId: java.util.UUID): List<MediaItemDto> =
        MomentMedia.selectAll()
            .where { MomentMedia.momentId eq momentId }
            .orderBy(MomentMedia.sortOrder, SortOrder.ASC)
            .map { it.toMediaDto() }

    private fun ResultRow.toMediaDto() = MediaItemDto(
        mediaUrl = this[MomentMedia.mediaUrl],
        mediaType = this[MomentMedia.mediaType],
        sortOrder = this[MomentMedia.sortOrder],
        width = this[MomentMedia.width],
        height = this[MomentMedia.height],
        durationMs = this[MomentMedia.durationMs],
    )

    private fun ResultRow.toDto(media: List<MediaItemDto>) = MomentDto(
        id = this[Moments.id].toString(),
        eventId = this[Moments.eventId]?.toString(),
        eventTitle = this.getOrNull(Events.title),
        authorId = this[Moments.authorId].toString(),
        authorNickname = this[Users.nickname],
        authorProfileImage = this[Users.profileImage],
        caption = this[Moments.caption],
        visibility = this[Moments.visibility],
        media = media,
        createdAt = this[Moments.createdAt].toString(),
    )
}
