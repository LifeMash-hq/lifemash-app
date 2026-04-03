package org.bmsk.lifemash.group

import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

class MembershipGuardImpl(
    private val groupRepository: GroupRepository,
    private val eventRepository: EventRepository,
) : MembershipGuard {
    override fun require(groupId: String, userId: String) {
        if (!groupRepository.isMember(Uuid.parse(groupId), Uuid.parse(userId))) {
            throw ForbiddenException("Not a member of this group")
        }
    }

    override fun requireByEvent(eventId: String, userId: String): String {
        val event = eventRepository.findById(Uuid.parse(eventId))
            ?: throw NotFoundException("Event not found")
        require(event.groupId, userId)
        return event.groupId
    }
}
