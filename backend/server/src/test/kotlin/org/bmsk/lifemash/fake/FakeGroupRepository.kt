package org.bmsk.lifemash.fake

import kotlin.time.Clock
import org.bmsk.lifemash.group.*
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.GroupMemberDto
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import java.util.*

class FakeGroupRepository : GroupRepository {
    private val groups = mutableMapOf<UUID, GroupData>()

    data class GroupData(
        val id: UUID,
        val name: String?,
        val type: String,
        val maxMembers: Int,
        val inviteCode: String,
        val members: MutableList<MemberData>,
        val createdAt: kotlinx.datetime.Instant,
    )

    data class MemberData(
        val userId: UUID,
        val nickname: String,
        val role: String,
        val joinedAt: kotlinx.datetime.Instant,
    )

    override fun create(userId: UUID, type: String, name: String?): GroupDto {
        val now = Clock.System.now()
        val maxMembers = if (type == "COUPLE") 2 else 50
        val inviteCode = (1..8).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
        val id = UUID.randomUUID()
        val data = GroupData(
            id = id, name = name, type = type, maxMembers = maxMembers,
            inviteCode = inviteCode,
            members = mutableListOf(MemberData(userId, "Owner", "OWNER", now)),
            createdAt = now,
        )
        groups[id] = data
        return data.toDto()
    }

    override fun join(userId: UUID, inviteCode: String): GroupDto {
        val data = groups.values.find { it.inviteCode == inviteCode }
            ?: throw NotFoundException("Group not found")

        if (data.members.size >= data.maxMembers) {
            throw ForbiddenException("Group is full")
        }

        if (data.members.none { it.userId == userId }) {
            data.members.add(MemberData(userId, "Member", "MEMBER", Clock.System.now()))
        }

        return data.toDto()
    }

    override fun findByUserId(userId: UUID): List<GroupDto> =
        groups.values.filter { data -> data.members.any { it.userId == userId } }.map { it.toDto() }

    override fun findById(groupId: UUID): GroupDto? = groups[groupId]?.toDto()

    override fun delete(groupId: UUID, userId: UUID) {
        val data = groups[groupId] ?: throw NotFoundException("Group not found")
        val member = data.members.find { it.userId == userId }
            ?: throw ForbiddenException("Not a member")
        if (member.role != "OWNER") throw ForbiddenException("Only OWNER can delete group")
        groups.remove(groupId)
    }

    override fun isMember(groupId: UUID, userId: UUID): Boolean =
        groups[groupId]?.members?.any { it.userId == userId } ?: false

    override fun getMemberUserIds(groupId: UUID): List<UUID> =
        groups[groupId]?.members?.map { it.userId } ?: emptyList()

    override fun updateName(groupId: UUID, name: String): GroupDto {
        val data = groups[groupId] ?: throw NotFoundException("Group not found")
        groups[groupId] = data.copy(name = name)
        return groups[groupId]!!.toDto()
    }

    private fun GroupData.toDto() = GroupDto(
        id = id.toString(),
        name = name,
        type = type,
        maxMembers = maxMembers,
        inviteCode = inviteCode,
        members = members.map {
            GroupMemberDto(
                userId = it.userId.toString(),
                nickname = it.nickname,
                profileImage = null,
                role = it.role,
                joinedAt = it.joinedAt,
            )
        },
        createdAt = createdAt,
    )
}
