package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import kotlin.uuid.Uuid

/**
 * 이벤트(일정) 데이터 영속성을 관리하는 Repository 인터페이스입니다.
 */
interface EventRepository {
    /** 특정 그룹의 주어진 연/월에 해당하는 이벤트 목록을 반환합니다. */
    fun getMonthEvents(groupId: Uuid, year: Int, month: Int): List<EventDto>
    /** 이벤트를 생성합니다. */
    fun create(groupId: Uuid, authorId: Uuid, request: CreateEventRequest): EventDto
    /** 기존 이벤트를 수정합니다. */
    fun update(eventId: Uuid, request: UpdateEventRequest): EventDto
    /** 이벤트를 삭제합니다. */
    fun delete(eventId: Uuid)
    /** ID로 이벤트를 조회합니다. */
    fun findById(eventId: Uuid): EventDto?

    /**
     * 식별자로 이벤트를 조회하여 상세 정보를 포함한 DTO(EventDetailDto)를 반환합니다.
     * @param eventId 조회할 이벤트 식별자
     * @param viewerId 조회를 요청한 유저의 식별자(참여 여부 계산에 사용)
     * @return 일치하는 이벤트가 없을 경우 null 반환
     */
    fun getEventDetail(eventId: Uuid, viewerId: Uuid): EventDetailDto?

    /**
     * 특정 이벤트에 사용자의 참가 상태를 토글(참가 안했으면 참가, 참가했으면 취소)합니다.
     * @param eventId 이벤트 식별자
     * @param userId 유저 식별자
     * @return 토글 후 현재 자신이 참가 상태인지 여부 (참가 = true)
     */
    fun toggleJoin(eventId: Uuid, userId: Uuid): Boolean
}

