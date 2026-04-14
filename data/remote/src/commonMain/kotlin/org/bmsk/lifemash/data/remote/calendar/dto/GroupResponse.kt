package org.bmsk.lifemash.data.remote.calendar.dto

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val id: String,
    val name: String?,
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
    val profileImage: String?,
    val role: String,
    val joinedAt: Instant,
)

@Serializable
data class CreateGroupRequest(val type: String, val name: String?)

@Serializable
data class JoinGroupRequest(val inviteCode: String)

@Serializable
data class UpdateGroupNameRequest(val name: String)
