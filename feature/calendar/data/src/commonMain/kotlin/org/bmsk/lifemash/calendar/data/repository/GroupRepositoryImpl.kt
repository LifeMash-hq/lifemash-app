package org.bmsk.lifemash.calendar.data.repository

import org.bmsk.lifemash.calendar.data.api.CalendarApi
import org.bmsk.lifemash.calendar.data.api.dto.CreateGroupBody
import org.bmsk.lifemash.calendar.data.api.dto.JoinGroupBody
import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

internal class GroupRepositoryImpl(private val api: CalendarApi) : GroupRepository {

    override suspend fun createGroup(type: GroupType, name: String?): Group =
        api.createGroup(CreateGroupBody(type = type, name = name)).toDomain()

    override suspend fun joinGroup(inviteCode: String): Group =
        api.joinGroup(JoinGroupBody(inviteCode = inviteCode)).toDomain()

    override suspend fun getMyGroups(): List<Group> =
        api.getMyGroups().map { it.toDomain() }

    override suspend fun getGroup(groupId: String): Group =
        api.getGroup(groupId).toDomain()

    override suspend fun deleteGroup(groupId: String) =
        api.deleteGroup(groupId)
}
