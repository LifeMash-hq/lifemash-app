package org.bmsk.lifemash.calendar.domain.repository

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType

interface GroupRepository {
    suspend fun createGroup(type: GroupType, name: String?): Group
    suspend fun joinGroup(inviteCode: String): Group
    suspend fun getMyGroups(): List<Group>
    suspend fun getGroup(groupId: String): Group
    suspend fun deleteGroup(groupId: String)
    suspend fun updateGroupName(groupId: String, name: String): Group
}
