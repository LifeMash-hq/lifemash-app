package org.bmsk.lifemash.explore

import org.bmsk.lifemash.db.tables.EventAttendees
import org.bmsk.lifemash.db.tables.Events
import org.bmsk.lifemash.db.tables.Follows
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.explore.EventSummaryDto
import org.bmsk.lifemash.model.explore.HeatmapDayDto
import org.bmsk.lifemash.model.explore.PublicEventDto
import org.bmsk.lifemash.model.explore.UserSuggestionDto
import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedExploreRepository : ExploreRepository {

    override fun searchUsers(query: String): List<UserSummaryDto> = transaction {
        val searchPattern = "%${query.lowercase()}%"
        Users.selectAll()
            .where { (Users.nickname.lowerCase() like searchPattern) or (Users.email.lowerCase() like searchPattern) }
            .limit(50)
            .map {
                UserSummaryDto(
                    id = it[Users.id].toString(),
                    nickname = it[Users.nickname],
                    profileImage = it[Users.profileImage]
                )
            }
    }

    override fun searchEvents(query: String): List<EventSummaryDto> = transaction {
        val searchPattern = "%${query.lowercase()}%"
        Events.selectAll()
            .where { (Events.title.lowerCase() like searchPattern) }
            .orderBy(Events.createdAt to SortOrder.DESC)
            .limit(50)
            .map {
                EventSummaryDto(
                    id = it[Events.id].toString(),
                    title = it[Events.title],
                    startAt = it[Events.startAt].toString(),
                    color = it[Events.color]
                )
            }
    }

    override fun getPublicEvents(category: String?, cursor: String?, limit: Int): List<PublicEventDto> = transaction {
        val offset = cursor?.toLongOrNull() ?: 0L
        val attendeeCountAlias = EventAttendees.eventId.count().alias("attendee_count")

        val rows = Events
            .join(Users, org.jetbrains.exposed.sql.JoinType.INNER, Events.authorId, Users.id)
            .select(Events.id, Events.title, Events.startAt, Events.endAt, Events.color, Users.nickname, attendeeCountAlias)
            .where {
                val base = Events.visibility eq "public"
                if (category != null) base and (Events.title.lowerCase() like "%${category.lowercase()}%")
                else base
            }
            .groupBy(Events.id, Users.nickname)
            .orderBy(Events.startAt to SortOrder.DESC)
            .limit(limit, offset)

        rows.map {
            PublicEventDto(
                id = it[Events.id].toString(),
                title = it[Events.title],
                startAt = it[Events.startAt].toString(),
                endAt = it[Events.endAt]?.toString(),
                color = it[Events.color],
                authorNickname = it[Users.nickname],
                attendeeCount = it[attendeeCountAlias].toInt(),
            )
        }
    }

    override fun getHeatmap(userId: Uuid, year: Int, month: Int): List<HeatmapDayDto> = transaction {
        val javaId = userId.toJavaUuid()
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1)
        val start = startDate.atStartOfDay().toInstant(ZoneOffset.UTC)
        val end = endDate.atStartOfDay().toInstant(ZoneOffset.UTC)

        // 본인이 작성한 이벤트 + 참가한 이벤트의 날짜별 카운트
        val authorEvents = Events.selectAll()
            .where {
                (Events.authorId eq javaId) and
                    (Events.startAt greaterEq java.time.OffsetDateTime.ofInstant(start, ZoneOffset.UTC)) and
                    (Events.startAt less java.time.OffsetDateTime.ofInstant(end, ZoneOffset.UTC))
            }
            .map { it[Events.startAt].toLocalDate().toString() }

        val joinedEventIds = EventAttendees.selectAll()
            .where { EventAttendees.userId eq javaId }
            .map { it[EventAttendees.eventId] }

        val joinedEventDates = if (joinedEventIds.isEmpty()) emptyList()
        else Events.selectAll()
            .where {
                (Events.id inList joinedEventIds) and
                    (Events.startAt greaterEq java.time.OffsetDateTime.ofInstant(start, ZoneOffset.UTC)) and
                    (Events.startAt less java.time.OffsetDateTime.ofInstant(end, ZoneOffset.UTC))
            }
            .map { it[Events.startAt].toLocalDate().toString() }

        (authorEvents + joinedEventDates)
            .groupBy { it }
            .map { (date, events) -> HeatmapDayDto(date = date, eventCount = events.size) }
            .sortedBy { it.date }
    }

    override fun getFollowSuggestions(userId: Uuid, limit: Int): List<UserSuggestionDto> = transaction {
        val javaId = userId.toJavaUuid()

        // 내가 이미 팔로우 중인 ID 목록
        val alreadyFollowing = Follows.selectAll()
            .where { Follows.followerId eq javaId }
            .map { it[Follows.followingId] }

        // 내가 팔로우하는 사람들이 팔로우하는 사람들 (2촌)
        val excludedIds = alreadyFollowing + javaId
        val suggestions = Follows.selectAll()
            .where {
                (Follows.followerId inList alreadyFollowing) and
                    not(Follows.followingId inList excludedIds)
            }
            .groupBy(Follows.followingId)
            .map { row ->
                val candidateId = row[Follows.followingId]
                candidateId to Follows.selectAll()
                    .where {
                        (Follows.followerId inList alreadyFollowing) and
                            (Follows.followingId eq candidateId)
                    }.count().toInt()
            }
            .sortedByDescending { it.second }
            .take(limit)

        if (suggestions.isEmpty()) return@transaction emptyList()

        val candidateIds = suggestions.map { it.first }
        val mutualCountMap = suggestions.toMap()

        Users.selectAll()
            .where { Users.id inList candidateIds }
            .map {
                val id = it[Users.id]
                UserSuggestionDto(
                    id = id.toString(),
                    nickname = it[Users.nickname],
                    profileImage = it[Users.profileImage],
                    mutualFollowCount = mutualCountMap[id] ?: 0,
                )
            }
            .sortedByDescending { it.mutualFollowCount }
    }
}
