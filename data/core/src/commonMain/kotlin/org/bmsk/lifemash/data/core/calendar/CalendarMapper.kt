package org.bmsk.lifemash.data.core.calendar

import org.bmsk.lifemash.data.remote.calendar.dto.CommentDto
import org.bmsk.lifemash.data.remote.calendar.dto.EventDto
import org.bmsk.lifemash.data.remote.calendar.dto.FollowerDto
import org.bmsk.lifemash.data.remote.calendar.dto.GroupDto
import org.bmsk.lifemash.data.remote.calendar.dto.GroupMemberDto
import org.bmsk.lifemash.domain.calendar.Comment
import org.bmsk.lifemash.domain.calendar.Event
import org.bmsk.lifemash.domain.calendar.EventVisibility
import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupMember
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.domain.calendar.MemberRole

internal fun EventDto.toDomain(): Event =
    Event(
        id = id,
        groupId = groupId,
        authorId = authorId,
        title = title,
        description = description,
        location = location,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        color = color,
        visibility = parseVisibility(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

private fun EventDto.parseVisibility(): EventVisibility = when (visibility) {
    "public" -> EventVisibility.Public
    "followers" -> EventVisibility.Followers
    "group" -> EventVisibility.Group(visibilityGroupId ?: "")
    "specific" -> EventVisibility.Specific(visibilityUserIds ?: emptyList())
    "private" -> EventVisibility.Private
    else -> EventVisibility.Followers
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
