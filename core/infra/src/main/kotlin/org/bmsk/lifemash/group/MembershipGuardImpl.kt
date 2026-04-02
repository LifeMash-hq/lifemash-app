package org.bmsk.lifemash.group

import org.bmsk.lifemash.plugins.ForbiddenException
import kotlin.uuid.Uuid

class MembershipGuardImpl(
    private val groupRepository: GroupRepository,
) : MembershipGuard {
    override fun require(groupId: String, userId: String) {
        if (!groupRepository.isMember(Uuid.parse(groupId), Uuid.parse(userId))) {
            throw ForbiddenException("Not a member of this group")
        }
    }
}
