package org.bmsk.lifemash.event

import kotlin.time.Instant
import org.bmsk.lifemash.db.tables.Comments
import org.bmsk.lifemash.db.tables.EventAttendees
import org.bmsk.lifemash.db.tables.Events
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventAttendeeDto
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.bmsk.lifemash.util.toOffsetDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
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
            it[Events.visibility] = request.visibility
            it[Events.visibilityGroupId] = request.visibilityGroupId?.let { id -> UUID.fromString(id) }
            it[Events.visibilityUserIds] = request.visibilityUserIds?.joinToString(",")
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
            request.visibility?.let { v -> it[visibility] = v }
            request.visibilityGroupId?.let { v -> it[visibilityGroupId] = UUID.fromString(v) }
            request.visibilityUserIds?.let { v -> it[visibilityUserIds] = v.joinToString(",") }
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

    override fun getEventDetail(eventId: Uuid, viewerId: Uuid): EventDetailDto? = transaction {
        val eventUuid = eventId.toJavaUuid()
        val viewerUuid = viewerId.toJavaUuid()

        val eventRow = (Events innerJoin Users).selectAll()
            .where { Events.id eq eventUuid }
            .singleOrNull() ?: return@transaction null

        val attendees = (EventAttendees innerJoin Users)
            .selectAll()
            .where { EventAttendees.eventId eq eventUuid }
            .map { row ->
                EventAttendeeDto(
                    id = row[Users.id].toString(),
                    nickname = row[Users.nickname],
                    profileImage = row[Users.profileImage],
                    status = row[EventAttendees.status],
                )
            }

        val attendeeUserIds = EventAttendees.selectAll()
            .where { EventAttendees.eventId eq eventUuid }
            .map { it[EventAttendees.userId] }

        val comments = (Comments innerJoin Users)
            .selectAll()
            .where { Comments.eventId eq eventUuid }
            .orderBy(Comments.createdAt)
            .map { row ->
                CommentDto(
                    id = row[Comments.id].toString(),
                    eventId = row[Comments.eventId].toString(),
                    authorId = row[Comments.authorId].toString(),
                    authorNickname = row[Users.nickname],
                    authorProfileImage = row[Users.profileImage],
                    content = row[Comments.content],
                    createdAt = row[Comments.createdAt].toKotlinxInstant()
                )
            }

        EventDetailDto(
            id = eventRow[Events.id].toString(),
            groupId = eventRow[Events.groupId].toString(),
            title = eventRow[Events.title],
            description = eventRow[Events.description],
            startAt = eventRow[Events.startAt].toKotlinxInstant(),
            endAt = eventRow[Events.endAt]?.toKotlinxInstant(),
            isAllDay = eventRow[Events.isAllDay],
            location = eventRow[Events.location],
            imageEmoji = eventRow[Events.imageEmoji],
            authorNickname = eventRow[Users.nickname],
            attendees = attendees,
            comments = comments,
            isJoined = attendeeUserIds.any { it == viewerUuid }
        )
    }

    override fun toggleJoin(eventId: Uuid, userId: Uuid): Boolean = transaction {
        val eventUuid = eventId.toJavaUuid()
        val userUuid = userId.toJavaUuid()

        val deletedCount = EventAttendees.deleteWhere {
            (EventAttendees.eventId eq eventUuid) and
                (EventAttendees.userId eq userUuid)
        }

        if (deletedCount > 0) {
            false
        } else {
            EventAttendees.insert {
                it[EventAttendees.eventId] = eventUuid
                it[EventAttendees.userId] = userUuid
                it[EventAttendees.joinedAt] = nowUtc()
                it[EventAttendees.status] = "attending"
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
        visibility = this[Events.visibility],
        visibilityGroupId = this[Events.visibilityGroupId]?.toString(),
        visibilityUserIds = this[Events.visibilityUserIds]?.split(",")?.filter { it.isNotBlank() },
        createdAt = this[Events.createdAt].toKotlinxInstant(),
        updatedAt = this[Events.updatedAt].toKotlinxInstant(),
    )

    private fun Instant.toOffset(): OffsetDateTime = toOffsetDateTime()

    private fun now(): OffsetDateTime = nowUtc()
}
