package org.bmsk.lifemash.event

import kotlin.time.Instant
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.bmsk.lifemash.util.toOffsetDateTime
import org.bmsk.lifemash.db.tables.Events
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedEventRepository : EventRepository {

    override fun getMonthEvents(groupId: Uuid, year: Int, month: Int): List<EventDto> = transaction {
        val start = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        val end = start.plusMonths(1)

        Events.selectAll().where {
            (Events.groupId eq groupId.toJavaUuid()) and
                (Events.startAt greaterEq start) and
                (Events.startAt less end)
        }.orderBy(Events.startAt).map { it.toDto() }
    }

    override fun create(groupId: Uuid, authorId: Uuid, request: CreateEventRequest): EventDto = transaction {
        val now = now()
        Events.insert {
            it[Events.groupId] = groupId.toJavaUuid()
            it[Events.authorId] = authorId.toJavaUuid()
            it[Events.title] = request.title
            it[Events.description] = request.description
            it[Events.startAt] = request.startAt.toOffset()
            it[Events.endAt] = request.endAt?.toOffset()
            it[Events.isAllDay] = request.isAllDay
            it[Events.color] = request.color
            it[Events.createdAt] = now
            it[Events.updatedAt] = now
        }.resultedValues!!.first().toDto()
    }

    override fun update(eventId: Uuid, request: UpdateEventRequest): EventDto = transaction {
        Events.update({ Events.id eq eventId.toJavaUuid() }) {
            request.title?.let { v -> it[title] = v }
            request.description?.let { v -> it[description] = v }
            request.startAt?.let { v -> it[startAt] = v.toOffset() }
            request.endAt?.let { v -> it[endAt] = v.toOffset() }
            request.isAllDay?.let { v -> it[isAllDay] = v }
            request.color?.let { v -> it[color] = v }
            it[updatedAt] = now()
        }
        findById(eventId)!!
    }

    override fun delete(eventId: Uuid) {
        transaction {
            Events.deleteWhere { Events.id eq eventId.toJavaUuid() }
        }
    }

    override fun findById(eventId: Uuid): EventDto? = transaction {
        Events.selectAll().where { Events.id eq eventId.toJavaUuid() }.singleOrNull()?.toDto()
    }

    private fun ResultRow.toDto() = EventDto(
        id = this[Events.id].toString(),
        groupId = this[Events.groupId].toString(),
        authorId = this[Events.authorId].toString(),
        title = this[Events.title],
        description = this[Events.description],
        startAt = this[Events.startAt].toKotlinxInstant(),
        endAt = this[Events.endAt]?.toKotlinxInstant(),
        isAllDay = this[Events.isAllDay],
        color = this[Events.color],
        createdAt = this[Events.createdAt].toKotlinxInstant(),
        updatedAt = this[Events.updatedAt].toKotlinxInstant(),
    )

    private fun Instant.toOffset(): OffsetDateTime = toOffsetDateTime()

    private fun now(): OffsetDateTime = nowUtc()
}
