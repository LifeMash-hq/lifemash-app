@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.fake

import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.model.calendar.GroupDto
import kotlin.time.Clock
import kotlin.uuid.Uuid

class FakeGroupRepository : GroupRepository {
    private val groups = mutableMapOf<Uuid, GroupDto>()
    private val inviteCodes = mutableMapOf<String, Uuid>()
    private val members = mutableSetOf<Pair<String, String>>()

    fun addMember(groupId: Uuid, userId: Uuid) {
        members.add(groupId.toString() to userId.toString())
    }

    fun addGroup(group: GroupDto) {
        val id = Uuid.parse(group.id)
        groups[id] = group
        inviteCodes[group.inviteCode] = id
    }

    override fun isMember(groupId: Uuid, userId: Uuid): Boolean =
        (groupId.toString() to userId.toString()) in members

    override fun getMemberUserIds(groupId: Uuid): List<Uuid> =
        members.filter { it.first == groupId.toString() }.map { Uuid.parse(it.second) }

    override fun create(userId: Uuid, type: String, name: String?): GroupDto {
        val id = Uuid.random()
        val inviteCode = Uuid.random().toString().take(8)
        val maxMembers = if (type == "COUPLE") 2 else 50
        val group = GroupDto(
            id = id.toString(),
            name = name,
            type = type,
            maxMembers = maxMembers,
            inviteCode = inviteCode,
            createdAt = Clock.System.now(),
        )
        groups[id] = group
        inviteCodes[inviteCode] = id
        members.add(id.toString() to userId.toString())
        return group
    }

    override fun join(userId: Uuid, inviteCode: String): GroupDto {
        val groupId = inviteCodes[inviteCode] ?: throw RuntimeException("Group not found: $inviteCode")
        members.add(groupId.toString() to userId.toString())
        return groups[groupId]!!
    }

    override fun findByUserId(userId: Uuid): List<GroupDto> {
        val userIdStr = userId.toString()
        return members
            .filter { it.second == userIdStr }
            .mapNotNull { groups[Uuid.parse(it.first)] }
    }

    override fun findById(groupId: Uuid): GroupDto? = groups[groupId]

    override fun delete(groupId: Uuid, userId: Uuid) {
        val group = groups.remove(groupId) ?: return
        inviteCodes.remove(group.inviteCode)
        members.removeAll { it.first == groupId.toString() }
    }

    override fun updateName(groupId: Uuid, name: String): GroupDto {
        val existing = groups[groupId] ?: throw RuntimeException("Group not found: $groupId")
        val updated = existing.copy(name = name)
        groups[groupId] = updated
        return updated
    }
}
