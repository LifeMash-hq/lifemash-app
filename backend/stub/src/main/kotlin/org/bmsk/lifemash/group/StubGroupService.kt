package org.bmsk.lifemash.group

import kotlinx.datetime.Instant
import org.bmsk.lifemash.model.calendar.CreateGroupRequest
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.JoinGroupRequest
import org.bmsk.lifemash.model.calendar.UpdateGroupRequest
import java.util.*

class StubGroupService : GroupService {
    private val epoch = Instant.fromEpochSeconds(0)

    private fun demoGroup(id: UUID = UUID.randomUUID(), name: String? = "Demo Group") = GroupDto(
        id = id.toString(),
        name = name,
        type = "COUPLE",
        maxMembers = 2,
        inviteCode = "DEMO-CODE",
        members = emptyList(),
        createdAt = epoch,
    )

    override fun create(userId: UUID, request: CreateGroupRequest): GroupDto =
        demoGroup(name = request.name)

    override fun join(userId: UUID, request: JoinGroupRequest): GroupDto =
        demoGroup()

    override fun getMyGroups(userId: UUID): List<GroupDto> =
        listOf(demoGroup())

    override fun getGroup(groupId: UUID): GroupDto =
        demoGroup(id = groupId)

    override fun delete(groupId: UUID, userId: UUID) {}

    override fun updateName(groupId: UUID, userId: UUID, request: UpdateGroupRequest): GroupDto =
        demoGroup(id = groupId, name = request.name)
}
