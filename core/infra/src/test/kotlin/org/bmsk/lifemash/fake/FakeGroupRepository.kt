package org.bmsk.lifemash.fake

import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.model.calendar.GroupDto
import kotlin.uuid.Uuid

class FakeGroupRepository : GroupRepository {
    private val members = mutableSetOf<Pair<String, String>>()

    fun addMember(groupId: Uuid, userId: Uuid) {
        members.add(groupId.toString() to userId.toString())
    }

    override fun isMember(groupId: Uuid, userId: Uuid): Boolean =
        (groupId.toString() to userId.toString()) in members

    override fun getMemberUserIds(groupId: Uuid): List<Uuid> =
        members.filter { it.first == groupId.toString() }.map { Uuid.parse(it.second) }

    override fun create(userId: Uuid, type: String, name: String?): GroupDto = TODO()
    override fun join(userId: Uuid, inviteCode: String): GroupDto = TODO()
    override fun findByUserId(userId: Uuid): List<GroupDto> = TODO()
    override fun findById(groupId: Uuid): GroupDto? = TODO()
    override fun delete(groupId: Uuid, userId: Uuid) = TODO()
    override fun updateName(groupId: Uuid, name: String): GroupDto = TODO()
}
