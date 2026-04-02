package org.bmsk.lifemash.fake

import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.plugins.ForbiddenException
import kotlin.uuid.Uuid

class FakeMembershipGuard : MembershipGuard {
    private val members = mutableSetOf<Pair<String, String>>()

    fun addMember(groupId: Uuid, userId: Uuid) {
        members.add(groupId.toString() to userId.toString())
    }

    override fun require(groupId: String, userId: String) {
        if ((groupId to userId) !in members) {
            throw ForbiddenException("Not a member of this group")
        }
    }
}
