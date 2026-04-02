package org.bmsk.lifemash.event

import org.bmsk.lifemash.group.MembershipGuard
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.notification.FcmService
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

/**
 * 일정 비즈니스 로직 서비스.
 *
 * 핵심 역할:
 * 1. 모든 일정 작업 전에 그룹 멤버십 검증 (MembershipGuard)
 * 2. 일정 생성 시 그룹 내 다른 멤버에게 FCM 푸시 알림 발송
 */
class EventServiceImpl(
    private val eventRepository: EventRepository,
    private val membershipGuard: MembershipGuard,
    private val fcmService: FcmService,
) : EventService {
    /** 월별 일정 조회 — 그룹 멤버만 조회 가능 */
    override fun getMonthEvents(groupId: String, userId: String, year: Int, month: Int): List<EventDto> {
        membershipGuard.require(groupId, userId)
        return eventRepository.getMonthEvents(Uuid.parse(groupId), year, month)
    }

    /** 일정 생성 후 그룹 내 다른 멤버에게 "새 일정" 알림 발송 */
    override fun create(groupId: String, userId: String, request: CreateEventRequest): EventDto {
        membershipGuard.require(groupId, userId)
        val event = eventRepository.create(Uuid.parse(groupId), Uuid.parse(userId), request)
        fcmService.notifyGroupExcept(Uuid.parse(groupId), Uuid.parse(userId), "새 일정", event.title)
        return event
    }

    override fun update(groupId: String, userId: String, eventId: String, request: UpdateEventRequest): EventDto {
        membershipGuard.require(groupId, userId)
        return eventRepository.update(Uuid.parse(eventId), request)
    }

    override fun delete(groupId: String, userId: String, eventId: String) {
        membershipGuard.require(groupId, userId)
        eventRepository.delete(Uuid.parse(eventId))
    }

    override fun getEventDetail(userId: String, eventId: String): EventDetailDto {
        val event = eventRepository.findById(Uuid.parse(eventId))
            ?: throw NotFoundException("Event not found")
        membershipGuard.require(event.groupId, userId)
        return eventRepository.getEventDetail(Uuid.parse(eventId), Uuid.parse(userId))
            ?: throw NotFoundException("Event not found or access denied")
    }

    override fun toggleJoin(userId: String, eventId: String): Boolean {
        val event = eventRepository.findById(Uuid.parse(eventId))
            ?: throw NotFoundException("Event not found")
        membershipGuard.require(event.groupId, userId)
        return eventRepository.toggleJoin(Uuid.parse(eventId), Uuid.parse(userId))
    }
}
