package org.bmsk.lifemash.group

import org.bmsk.lifemash.model.calendar.CreateGroupRequest
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.model.calendar.JoinGroupRequest
import org.bmsk.lifemash.model.calendar.UpdateGroupRequest
import java.util.*

interface GroupService {
    fun create(userId: UUID, request: CreateGroupRequest): GroupDto
    fun join(userId: UUID, request: JoinGroupRequest): GroupDto
    fun getMyGroups(userId: UUID): List<GroupDto>
    fun getGroup(groupId: UUID): GroupDto
    fun delete(groupId: UUID, userId: UUID)
    fun updateName(groupId: UUID, userId: UUID, request: UpdateGroupRequest): GroupDto
}
