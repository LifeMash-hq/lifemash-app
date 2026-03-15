package org.bmsk.lifemash.calendar.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GroupMember(
    val userId: String,
    val nickname: String,
    val profileImage: String?,
    val role: MemberRole,
    val joinedAt: Instant,
)

@Serializable
enum class MemberRole { OWNER, MEMBER }
