package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest

/**
 * 이벤트 관련 비즈니스 로직을 처리하는 서비스 인터페이스.
 */
interface EventService {
    /** 특정 그룹의 주어진 연/월에 해당하는 이벤트 목록을 반환합니다. */
    fun getMonthEvents(groupId: String, userId: String, year: Int, month: Int): List<EventDto>
    /** 이벤트를 생성합니다. */
    fun create(groupId: String, userId: String, request: CreateEventRequest): EventDto
    /** 기존 이벤트를 수정합니다. */
    fun update(groupId: String, userId: String, eventId: String, request: UpdateEventRequest): EventDto
    /** 이벤트를 삭제합니다. */
    fun delete(groupId: String, userId: String, eventId: String)

    /** 
     * 식별자로 이벤트를 단건 조회하여 상세 정보(댓글, 참가자, 참가여부)를 반환합니다.
     * @param userId 조회를 요청하는 유저의 식별자
     * @param eventId 조회할 이벤트의 식별자
     */
    fun getEventDetail(userId: String, eventId: String): EventDetailDto

    /**
     * 특정 이벤트에 대해 유저의 참여 상태를 토글(참여/취소)합니다.
     * @param userId 참여 상태를 변경할 유저의 식별자
     * @param eventId 대상 이벤트 식별자
     * @return 변경 후 유저의 참여 여부 (true = 참여, false = 미참여)
     */
    fun toggleJoin(userId: String, eventId: String): Boolean
}
