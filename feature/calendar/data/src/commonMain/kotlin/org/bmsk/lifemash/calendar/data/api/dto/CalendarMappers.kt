package org.bmsk.lifemash.calendar.data.api.dto

import org.bmsk.lifemash.calendar.domain.model.Comment
import org.bmsk.lifemash.calendar.domain.model.Event
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupMember
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.model.MemberRole
import org.bmsk.lifemash.model.calendar.CommentDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.GroupMemberDto

fun EventDto.toDomain() = Event(
    id = id, groupId = groupId, authorId = authorId, title = title,
    description = description, startAt = startAt, endAt = endAt,
    isAllDay = isAllDay, color = color, createdAt = createdAt, updatedAt = updatedAt,
)

fun CommentDto.toDomain() = Comment(
    id = id, eventId = eventId, authorId = authorId,
    content = content, createdAt = createdAt,
)

fun GroupDto.toDomain() = Group(
    id = id, name = name, type = GroupType.valueOf(type),
    maxMembers = maxMembers, inviteCode = inviteCode,
    members = members.map { it.toDomain() }, createdAt = createdAt,
)

fun GroupMemberDto.toDomain() = GroupMember(
    userId = userId, nickname = nickname, profileImage = profileImage,
    role = MemberRole.valueOf(role), joinedAt = joinedAt,
)
