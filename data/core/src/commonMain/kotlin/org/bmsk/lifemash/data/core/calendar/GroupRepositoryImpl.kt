package org.bmsk.lifemash.data.core.calendar

import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.domain.calendar.GroupType
import org.bmsk.lifemash.data.remote.calendar.CalendarApi
import org.bmsk.lifemash.data.remote.calendar.dto.CreateGroupRequest
import org.bmsk.lifemash.data.remote.calendar.dto.JoinGroupRequest
import org.bmsk.lifemash.data.remote.calendar.dto.UpdateGroupNameRequest

internal class GroupRepositoryImpl(private val api: CalendarApi) : GroupRepository {

    override suspend fun createGroup(type: GroupType, name: String?): Group =
        api.createGroup(CreateGroupRequest(type = type.name, name = name)).toDomain()

    override suspend fun joinGroup(inviteCode: String): Group =
        api.joinGroup(JoinGroupRequest(inviteCode = inviteCode)).toDomain()

    override suspend fun getMyGroups(): List<Group> =
        api.getMyGroups().map { it.toDomain() }

    override suspend fun getGroup(groupId: String): Group =
        api.getGroup(groupId).toDomain()

    override suspend fun deleteGroup(groupId: String) =
        api.deleteGroup(groupId)

    override suspend fun updateGroupName(groupId: String, name: String): Group =
        api.updateGroupName(groupId, UpdateGroupNameRequest(name)).toDomain()
}
