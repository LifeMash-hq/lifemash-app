package org.bmsk.lifemash.fake

import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.plugins.ForbiddenException
import kotlin.uuid.Uuid

class FakeMembershipGuard : MembershipGuard {
    private val members = mutableSetOf<Pair<String, String>>()
    // eventId → groupId 매핑 (requireByEvent 테스트용)
    private val eventGroups = mutableMapOf<String, String>()

    fun addMember(groupId: Uuid, userId: Uuid) {
        members.add(groupId.toString() to userId.toString())
    }

    fun addEventGroup(eventId: Uuid, groupId: Uuid) {
        eventGroups[eventId.toString()] = groupId.toString()
    }

    override fun require(groupId: String, userId: String) {
        if ((groupId to userId) !in members) {
            throw ForbiddenException("Not a member of this group")
        }
    }

    override fun requireByEvent(eventId: String, userId: String): String {
        val groupId = eventGroups[eventId] ?: throw org.bmsk.lifemash.plugins.NotFoundException("Event not found")
        require(groupId, userId)
        return groupId
    }
}
