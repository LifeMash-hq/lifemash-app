package org.bmsk.lifemash.domain.calendar

interface GroupRepository {
    suspend fun createGroup(type: GroupType, name: String?): Group
    suspend fun joinGroup(inviteCode: String): Group
    suspend fun getMyGroups(): List<Group>
    suspend fun getGroup(groupId: String): Group
    suspend fun deleteGroup(groupId: String)
    suspend fun updateGroupName(groupId: String, name: String): Group
}
