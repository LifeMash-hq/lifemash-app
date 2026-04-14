package org.bmsk.lifemash.domain.calendar

import kotlin.time.Instant
import kotlinx.serialization.Serializable

data class GroupMember(
    val userId: String,
    val nickname: String,
    val profileImage: String?,
    val role: MemberRole,
    val joinedAt: Instant,
)

@Serializable
enum class MemberRole { OWNER, MEMBER }
