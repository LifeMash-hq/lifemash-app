package org.bmsk.lifemash.event

import kotlin.time.Clock
import org.bmsk.lifemash.fake.FakeEventRepository
import org.bmsk.lifemash.fake.FakeFcmService
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.fake.FakeGroupRepository
import org.bmsk.lifemash.plugins.ForbiddenException
import java.util.*
import kotlin.test.*

class EventServiceTest {

    private lateinit var eventRepo: FakeEventRepository
    private lateinit var groupRepo: FakeGroupRepository
    private lateinit var fcmService: FakeFcmService
    private lateinit var service: EventService

    private val userId = UUID.randomUUID()
    private val nonMemberId = UUID.randomUUID()
    private lateinit var groupId: String

    @BeforeTest
    fun setUp() {
        eventRepo = FakeEventRepository()
        groupRepo = FakeGroupRepository()
        fcmService = FakeFcmService()
        service = EventServiceImpl(eventRepo, groupRepo, fcmService)
        groupId = groupRepo.create(userId, "COUPLE", "테스트그룹").id
    }

    @Test
    fun `그룹 멤버가 월별 일정을 조회한다`() {
        // Given
        val request = CreateEventRequest(title = "회의", startAt = Clock.System.now())
        eventRepo.create(UUID.fromString(groupId), userId, request)

        // When
        val now = Clock.System.now()
        val year = now.toString().substring(0, 4).toInt()
        val month = now.toString().substring(5, 7).toInt()
        val events = service.getMonthEvents(groupId, userId.toString(), year, month)

        // Then
        assertEquals(1, events.size)
        assertEquals("회의", events[0].title)
    }

    @Test
    fun `비멤버가 일정 조회 시 ForbiddenException이 발생한다`() {
        // When & Then
        assertFailsWith<ForbiddenException> {
            service.getMonthEvents(groupId, nonMemberId.toString(), 2026, 3)
        }
    }

    @Test
    fun `일정 생성 시 그룹 멤버에게 FCM 알림이 발송된다`() {
        // Given
        val request = CreateEventRequest(title = "점심 약속", startAt = Clock.System.now())

        // When
        service.create(groupId, userId.toString(), request)

        // Then
        assertEquals(1, fcmService.notifications.size)
        assertEquals("새 일정", fcmService.notifications[0].title)
        assertEquals("점심 약속", fcmService.notifications[0].body)
    }

    @Test
    fun `일정 생성 시 작성자는 알림 대상에서 제외된다`() {
        // Given
        val request = CreateEventRequest(title = "저녁", startAt = Clock.System.now())

        // When
        service.create(groupId, userId.toString(), request)

        // Then
        assertEquals(userId, fcmService.notifications[0].excludeUserId)
    }

    @Test
    fun `비멤버가 일정 생성 시 ForbiddenException이 발생한다`() {
        // Given
        val request = CreateEventRequest(title = "일정", startAt = Clock.System.now())

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.create(groupId, nonMemberId.toString(), request)
        }
    }

    @Test
    fun `일정을 부분 수정한다`() {
        // Given
        val created = service.create(groupId, userId.toString(), CreateEventRequest(title = "원래제목", startAt = Clock.System.now()))

        // When
        val updated = service.update(groupId, userId.toString(), created.id, UpdateEventRequest(title = "변경제목"))

        // Then
        assertEquals("변경제목", updated.title)
    }

    @Test
    fun `일정을 삭제한다`() {
        // Given
        val created = service.create(groupId, userId.toString(), CreateEventRequest(title = "삭제할일정", startAt = Clock.System.now()))

        // When
        service.delete(groupId, userId.toString(), created.id)

        // Then
        assertNull(eventRepo.findById(UUID.fromString(created.id)))
    }

    @Test
    fun `비멤버가 일정 삭제 시 ForbiddenException이 발생한다`() {
        // Given
        val created = service.create(groupId, userId.toString(), CreateEventRequest(title = "일정", startAt = Clock.System.now()))

        // When & Then
        assertFailsWith<ForbiddenException> {
            service.delete(groupId, nonMemberId.toString(), created.id)
        }
    }
}
