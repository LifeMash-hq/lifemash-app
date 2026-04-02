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

    override fun getEventDetail(eventId: Uuid, viewerId: Uuid): org.bmsk.lifemash.model.calendar.EventDetailDto? = transaction {
        val eventUuid = eventId.toJavaUuid()
        val viewerUuid = viewerId.toJavaUuid()

        val eventRow = (Events innerJoin org.bmsk.lifemash.db.tables.Users).selectAll().where { Events.id eq eventUuid }.singleOrNull() ?: return@transaction null

        val attendees = (org.bmsk.lifemash.db.tables.EventAttendees innerJoin org.bmsk.lifemash.db.tables.Users)
            .selectAll()
            .where { org.bmsk.lifemash.db.tables.EventAttendees.eventId eq eventUuid }
            .map { row ->
                org.bmsk.lifemash.model.calendar.EventAttendeeDto(
                    id = row[org.bmsk.lifemash.db.tables.Users.id].toString(),
                    nickname = row[org.bmsk.lifemash.db.tables.Users.nickname],
                    profileImage = row[org.bmsk.lifemash.db.tables.Users.profileImage]
                )
            }
        
        val attendeeUserIds = (org.bmsk.lifemash.db.tables.EventAttendees).selectAll().where { org.bmsk.lifemash.db.tables.EventAttendees.eventId eq eventUuid }.map { it[org.bmsk.lifemash.db.tables.EventAttendees.userId] }

        val comments = (org.bmsk.lifemash.db.tables.Comments innerJoin org.bmsk.lifemash.db.tables.Users)
            .selectAll()
            .where { org.bmsk.lifemash.db.tables.Comments.eventId eq eventUuid }
            .orderBy(org.bmsk.lifemash.db.tables.Comments.createdAt)
            .map { row ->
                org.bmsk.lifemash.model.calendar.CommentDto(
                    id = row[org.bmsk.lifemash.db.tables.Comments.id].toString(),
                    eventId = row[org.bmsk.lifemash.db.tables.Comments.eventId].toString(),
                    authorId = row[org.bmsk.lifemash.db.tables.Comments.authorId].toString(),
                    authorNickname = row[org.bmsk.lifemash.db.tables.Users.nickname],
                    authorProfileImage = row[org.bmsk.lifemash.db.tables.Users.profileImage],
                    content = row[org.bmsk.lifemash.db.tables.Comments.content],
                    createdAt = row[org.bmsk.lifemash.db.tables.Comments.createdAt].toKotlinxInstant()
                )
            }

        org.bmsk.lifemash.model.calendar.EventDetailDto(
            id = eventRow[Events.id].toString(),
            groupId = eventRow[Events.groupId].toString(),
            title = eventRow[Events.title],
            description = eventRow[Events.description],
            startAt = eventRow[Events.startAt].toKotlinxInstant(),
            endAt = eventRow[Events.endAt]?.toKotlinxInstant(),
            isAllDay = eventRow[Events.isAllDay],
            location = eventRow[Events.location],
            imageEmoji = eventRow[Events.imageEmoji],
            authorNickname = eventRow[org.bmsk.lifemash.db.tables.Users.nickname],
            attendees = attendees,
            comments = comments,
            isJoined = attendeeUserIds.any { it == viewerUuid }
        )
    }

    override fun toggleJoin(eventId: Uuid, userId: Uuid): Boolean = transaction {
        val eventUuid = eventId.toJavaUuid()
        val userUuid = userId.toJavaUuid()
        
        val deletedCount = org.bmsk.lifemash.db.tables.EventAttendees.deleteWhere {
            (org.bmsk.lifemash.db.tables.EventAttendees.eventId eq eventUuid) and 
            (org.bmsk.lifemash.db.tables.EventAttendees.userId eq userUuid)
        }
        
        if (deletedCount > 0) {
            false
        } else {
            org.bmsk.lifemash.db.tables.EventAttendees.insert {
                it[org.bmsk.lifemash.db.tables.EventAttendees.eventId] = eventUuid
                it[org.bmsk.lifemash.db.tables.EventAttendees.userId] = userUuid
                it[org.bmsk.lifemash.db.tables.EventAttendees.joinedAt] = org.bmsk.lifemash.util.nowUtc()
            }
            true
        }
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
        location = this[Events.location],
        imageEmoji = this[Events.imageEmoji],
        createdAt = this[Events.createdAt].toKotlinxInstant(),
        updatedAt = this[Events.updatedAt].toKotlinxInstant(),
    )

    private fun Instant.toOffset(): OffsetDateTime = toOffsetDateTime()

    private fun now(): OffsetDateTime = nowUtc()
}
