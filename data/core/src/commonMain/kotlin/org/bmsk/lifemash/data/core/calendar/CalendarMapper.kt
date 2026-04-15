package org.bmsk.lifemash.data.core.calendar

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.bmsk.lifemash.data.remote.calendar.dto.CommentDto
import org.bmsk.lifemash.data.remote.calendar.dto.EventDto
import org.bmsk.lifemash.data.remote.calendar.dto.FollowerDto
import org.bmsk.lifemash.data.remote.calendar.dto.GroupDto
import org.bmsk.lifemash.data.remote.calendar.dto.GroupMemberDto
import org.bmsk.lifemash.domain.calendar.Comment
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventTiming
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupMember
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.calendar.MemberRole

private val ALL_DAY_TZ = TimeZone.UTC

internal fun EventDto.toDomain(): Event =
    Event(
        id = id,
        groupId = groupId,
        authorId = authorId,
        title = title,
        description = description,
        location = location,
        timing = toTiming(),
        color = color,
        visibility = parseVisibility(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

internal fun toTiming(startAt: Instant, endAt: Instant?, isAllDay: Boolean, eventId: String): EventTiming =
    if (isAllDay) {
        EventTiming.AllDay(startAt.toLocalDateTime(ALL_DAY_TZ).date)
    } else {
        EventTiming.Timed(
            start = startAt,
            end = requireNotNull(endAt) { "Timed event must have endAt (id=$eventId)" },
        )
    }

private fun EventDto.toTiming(): EventTiming = toTiming(startAt, endAt, isAllDay, id)

internal data class TimingRequest(val startAt: Instant, val endAt: Instant?, val isAllDay: Boolean)

internal fun EventTiming.toRequestFields(): TimingRequest = when (this) {
    is EventTiming.Timed -> TimingRequest(start, end, false)
    is EventTiming.AllDay -> TimingRequest(date.atStartOfDayIn(ALL_DAY_TZ), null, true)
}

private fun EventDto.parseVisibility(): EventVisibility = when (visibility) {
    "public" -> EventVisibility.Public
    "followers" -> EventVisibility.Followers
    "group" -> EventVisibility.Group(visibilityGroupId ?: "")
    "specific" -> EventVisibility.Specific(visibilityUserIds ?: emptyList())
    "private" -> EventVisibility.Private
    else -> EventVisibility.Followers
}

internal data class VisibilityRequest(
    val type: String,
    val groupId: String? = null,
    val userIds: List<String>? = null,
)

internal fun EventVisibility.toRequestFields(): VisibilityRequest = when (this) {
    is EventVisibility.Public -> VisibilityRequest("public")
    is EventVisibility.Followers -> VisibilityRequest("followers")
    is EventVisibility.Group -> VisibilityRequest("group", groupId = groupId)
    is EventVisibility.Specific -> VisibilityRequest("specific", userIds = userIds)
    is EventVisibility.Private -> VisibilityRequest("private")
}

internal fun GroupDto.toDomain(): Group =
    Group(
        id = id,
        name = name,
        type = GroupType.valueOf(type),
        maxMembers = maxMembers,
        inviteCode = inviteCode,
        members = members.map { it.toDomain() },
        createdAt = createdAt,
    )

internal fun GroupMemberDto.toDomain(): GroupMember =
    GroupMember(
        userId = userId,
        nickname = nickname,
        profileImage = profileImage,
        role = MemberRole.valueOf(role),
        joinedAt = joinedAt,
    )

internal fun CommentDto.toDomain(): Comment =
    Comment(
        id = id,
        eventId = eventId,
        authorId = authorId,
        content = content,
        createdAt = createdAt,
    )

internal fun FollowerDto.toDomain(): Follower =
    Follower(
        id = id,
        nickname = nickname,
        profileImage = profileImage,
    )
