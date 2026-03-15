package org.bmsk.lifemash.calendar.data.api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupMember
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.model.MemberRole

@Serializable
data class GroupDto(
    val id: String,
    val name: String?,
    val type: GroupType,
    val maxMembers: Int,
    val inviteCode: String,
    val members: List<GroupMemberDto> = emptyList(),
    val createdAt: Instant,
) {
    fun toDomain() = Group(
        id = id,
        name = name,
        type = type,
        maxMembers = maxMembers,
        inviteCode = inviteCode,
        members = members.map { it.toDomain() },
        createdAt = createdAt,
    )
}

@Serializable
data class GroupMemberDto(
    val userId: String,
    val nickname: String,
    val profileImage: String?,
    val role: MemberRole,
    val joinedAt: Instant,
) {
    fun toDomain() = GroupMember(
        userId = userId,
        nickname = nickname,
        profileImage = profileImage,
        role = role,
        joinedAt = joinedAt,
    )
}

@Serializable
data class CreateGroupBody(val type: GroupType, val name: String?)

@Serializable
data class JoinGroupBody(val inviteCode: String)
