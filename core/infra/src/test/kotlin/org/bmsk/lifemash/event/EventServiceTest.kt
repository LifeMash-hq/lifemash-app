@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
package org.bmsk.lifemash.event

import kotlin.time.Clock
import kotlin.uuid.Uuid
import org.bmsk.lifemash.fake.FakeEventRepository
import org.bmsk.lifemash.fake.FakeFcmService
import org.bmsk.lifemash.fake.FakeMembershipGuard
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.test.*

class EventServiceTest {
    private lateinit var eventRepository: FakeEventRepository
    private lateinit var membershipGuard: FakeMembershipGuard
    private lateinit var fcmService: FakeFcmService
    private lateinit var eventService: EventService

    @BeforeTest
    fun setup() {
        eventRepository = FakeEventRepository()
        membershipGuard = FakeMembershipGuard()
        fcmService = FakeFcmService()
        eventService = EventServiceImpl(eventRepository, membershipGuard, fcmService)
    }

    @Test
    fun `getEventDetail - 성공 시 DTO를 반환한다`() {
        // Given
        val eventId = Uuid.random()
        val userId = Uuid.random()
        val groupId = Uuid.random()
        val now = Clock.System.now()
        val mockDetail = EventDetailDto(
            id = eventId.toString(),
            groupId = groupId.toString(),
            title = "Test Event",
            description = "Test Description",
            startAt = now,
            endAt = null,
            isAllDay = false,
            location = "Test Location",
            imageEmoji = "📅",
            authorNickname = "Author",
            attendees = emptyList(),
            comments = emptyList(),
            isJoined = false
        )
        val mockEvent = EventDto(
            id = eventId.toString(),
            groupId = groupId.toString(),
            authorId = userId.toString(),
            title = "Test Event",
            startAt = now,
            createdAt = now,
            updatedAt = now,
        )
        eventRepository.addEvent(mockEvent)
        eventRepository.setEventDetail(eventId, mockDetail)
        membershipGuard.addMember(groupId, userId)
        membershipGuard.addEventGroup(eventId, groupId)

        // When
        val result = eventService.getEventDetail(userId.toString(), eventId.toString())

        // Then
        assertEquals(eventId.toString(), result.id)
        assertEquals("Test Event", result.title)
    }

    @Test
    fun `getEventDetail - 존재하지 않는 이벤트 조회 시 NotFoundException 발생`() {
        // Given
        val eventId = Uuid.random()
        val userId = Uuid.random()

        // When & Then
        assertFailsWith<NotFoundException> {
            eventService.getEventDetail(userId.toString(), eventId.toString())
        }
    }

    @Test
    fun `toggleJoin - 참여 상태를 정상적으로 토글한다`() {
        // Given
        val eventId = Uuid.random()
        val userId = Uuid.random()
        val groupId = Uuid.random()
        val now = Clock.System.now()
        val mockEvent = EventDto(
            id = eventId.toString(),
            groupId = groupId.toString(),
            authorId = userId.toString(),
            title = "Test Event",
            startAt = now,
            createdAt = now,
            updatedAt = now,
        )
        eventRepository.addEvent(mockEvent)
        membershipGuard.addMember(groupId, userId)
        membershipGuard.addEventGroup(eventId, groupId)

        // When (First toggle: join)
        val firstResult = eventService.toggleJoin(userId.toString(), eventId.toString())
        // Then
        assertTrue(firstResult)

        // When (Second toggle: unjoin)
        val secondResult = eventService.toggleJoin(userId.toString(), eventId.toString())
        // Then
        assertFalse(secondResult)
    }

    @Test
    fun `toggleJoin - 존재하지 않는 이벤트에 대한 토글 시 NotFoundException 발생`() {
        // Given
        val eventId = Uuid.random()
        val userId = Uuid.random()

        // When & Then
        assertFailsWith<NotFoundException> {
            eventService.toggleJoin(userId.toString(), eventId.toString())
        }
    }
}
