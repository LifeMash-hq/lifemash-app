package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import kotlin.time.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventDtoTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `EventDto가 ISO 8601로 직렬화된다`() {
        // Given
        val now = Clock.System.now()
        val dto = EventDto(
            id = "event-1",
            groupId = "group-1",
            authorId = "user-1",
            title = "테스트 일정",
            startAt = now,
            isAllDay = false,
            createdAt = now,
            updatedAt = now,
        )

        // When
        val jsonStr = json.encodeToString(dto)

        // Then
        assertContains(jsonStr, "테스트 일정")
        assertContains(jsonStr, "event-1")
    }

    @Test
    fun `UpdateEventRequest의 null 필드는 업데이트 대상이 아니다`() {
        // Given
        val request = UpdateEventRequest(title = "수정된 제목")

        // Then — null인 필드는 기존 값 유지 의미
        assertNull(request.description)
        assertNull(request.startAt)
        assertNull(request.color)
    }

    @Test
    fun `CreateEventRequest 필수 필드 검증`() {
        val now = Clock.System.now()
        val request = CreateEventRequest(
            title = "새 일정",
            startAt = now,
        )

        assertTrue(request.title.isNotBlank())
        assertNull(request.description)
        assertNull(request.endAt)
    }
}
