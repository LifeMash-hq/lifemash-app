package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.GroupDto
import java.util.*

interface GroupRepository {
    fun create(userId: UUID, type: String, name: String?): GroupDto
    fun join(userId: UUID, inviteCode: String): GroupDto
    fun findByUserId(userId: UUID): List<GroupDto>
    fun findById(groupId: UUID): GroupDto?
    fun delete(groupId: UUID, userId: UUID)
    fun isMember(groupId: UUID, userId: UUID): Boolean
    fun getMemberUserIds(groupId: UUID): List<UUID>
    fun updateName(groupId: UUID, name: String): GroupDto
}
