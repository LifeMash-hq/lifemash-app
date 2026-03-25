package org.bmsk.lifemash.model.calendar

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val id: String,
    val name: String? = null,
    val type: String,
    val maxMembers: Int,
    val inviteCode: String,
    val members: List<GroupMemberDto> = emptyList(),
    val createdAt: Instant,
)

@Serializable
data class GroupMemberDto(
    val userId: String,
    val nickname: String,
    val profileImage: String? = null,
    val role: String,
    val joinedAt: Instant,
)

@Serializable
data class CreateGroupRequest(
    val type: String = "COUPLE",
    val name: String? = null,
)

@Serializable
data class JoinGroupRequest(val inviteCode: String)

@Serializable
data class UpdateGroupRequest(val name: String)
