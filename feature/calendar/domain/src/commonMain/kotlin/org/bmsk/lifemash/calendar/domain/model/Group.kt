package org.bmsk.lifemash.calendar.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val name: String?,
    val type: GroupType,
    val maxMembers: Int,
    val inviteCode: String,
    val members: List<GroupMember> = emptyList(),
    val createdAt: Instant,
)

@Serializable
enum class GroupType { COUPLE, FAMILY, FRIENDS, TEAM }
