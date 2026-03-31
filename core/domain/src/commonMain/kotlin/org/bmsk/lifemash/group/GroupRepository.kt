package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.GroupDto
import kotlin.uuid.Uuid

interface GroupRepository {
    fun create(userId: Uuid, type: String, name: String?): GroupDto
    fun join(userId: Uuid, inviteCode: String): GroupDto
    fun findByUserId(userId: Uuid): List<GroupDto>
    fun findById(groupId: Uuid): GroupDto?
    fun delete(groupId: Uuid, userId: Uuid)
    fun isMember(groupId: Uuid, userId: Uuid): Boolean
    fun getMemberUserIds(groupId: Uuid): List<Uuid>
    fun updateName(groupId: Uuid, name: String): GroupDto
}
