package org.bmsk.lifemash.group

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.GroupDto
import java.util.*

class StubGroupRepository : GroupRepository {
    private val epoch = Instant.fromEpochSeconds(0)
    private val groups = mutableMapOf<UUID, GroupDto>()

    private fun demoGroup(id: UUID, name: String? = "Demo Group") = GroupDto(
        id = id.toString(),
        name = name,
        type = "COUPLE",
        maxMembers = 2,
        inviteCode = "DEMO-${id.toString().take(6)}",
        members = emptyList(),
        createdAt = epoch,
    )

    override fun create(userId: UUID, type: String, name: String?): GroupDto {
        val id = UUID.randomUUID()
        val group = demoGroup(id, name)
        groups[id] = group
        return group
    }

    override fun join(userId: UUID, inviteCode: String): GroupDto =
        groups.values.firstOrNull() ?: demoGroup(UUID.randomUUID())

    override fun findByUserId(userId: UUID): List<GroupDto> =
        groups.values.toList()

    override fun findById(groupId: UUID): GroupDto? =
        groups[groupId] ?: demoGroup(groupId)

    override fun delete(groupId: UUID, userId: UUID) {
        groups.remove(groupId)
    }

    override fun isMember(groupId: UUID, userId: UUID): Boolean = true

    override fun getMemberUserIds(groupId: UUID): List<UUID> = emptyList()

    override fun updateName(groupId: UUID, name: String): GroupDto {
        val updated = demoGroup(groupId, name)
        groups[groupId] = updated
        return updated
    }
}
