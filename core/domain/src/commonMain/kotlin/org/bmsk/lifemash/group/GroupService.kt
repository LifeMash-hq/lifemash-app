package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.CreateGroupRequest
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.JoinGroupRequest
import org.bmsk.lifemash.model.calendar.UpdateGroupRequest
import kotlin.uuid.Uuid

interface GroupService {
    fun create(userId: Uuid, request: CreateGroupRequest): GroupDto
    fun join(userId: Uuid, request: JoinGroupRequest): GroupDto
    fun getMyGroups(userId: Uuid): List<GroupDto>
    fun getGroup(groupId: Uuid): GroupDto
    fun delete(groupId: Uuid, userId: Uuid)
    fun updateName(groupId: Uuid, userId: Uuid, request: UpdateGroupRequest): GroupDto
}
