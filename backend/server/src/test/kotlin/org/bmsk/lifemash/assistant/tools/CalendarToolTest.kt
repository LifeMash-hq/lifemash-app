package org.bmsk.lifemash.assistant.tools

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.*
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.fake.FakeEventRepository
import org.bmsk.lifemash.fake.FakeGroupRepository
import org.bmsk.lifemash.fake.fakeEventService
import java.util.*
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class CalendarToolTest {

    private lateinit var eventRepo: FakeEventRepository
    private lateinit var groupRepo: FakeGroupRepository
    private lateinit var tool: CalendarTool

    private val userId = UUID.randomUUID()

    @BeforeTest
    fun setUp() {
        eventRepo = FakeEventRepository()
        groupRepo = FakeGroupRepository()
        tool = CalendarTool(eventRepo, groupRepo, fakeEventService())
    }

    @Test
    fun `그룹이 없으면 빈 일정 결과를 반환한다`() {
        // When
        val result = tool.executeTool(userId, "get_today_events", buildJsonObject { })

        // Then
        assertTrue(result.contains("가입된 그룹이 없습니다"))
    }

    @Test
    fun `월별 일정을 조회한다`() {
        // Given
        val group = groupRepo.create(userId, "COUPLE", "테스트")
        val now = Clock.System.now()
        val year = now.toString().substring(0, 4).toInt()
        val month = now.toString().substring(5, 7).toInt()
        eventRepo.create(UUID.fromString(group.id), userId, CreateEventRequest(title = "일정1", startAt = now))

        // When
        val result = tool.executeTool(userId, "get_month_events", buildJsonObject {
            put("year", year)
            put("month", month)
        })

        // Then
        val json = Json.parseToJsonElement(result).jsonObject
        val events = json["events"]!!.jsonArray
        assertEquals(1, events.size)
    }

    @Test
    fun `year 파라미터가 없으면 에러를 반환한다`() {
        // When
        val result = tool.executeTool(userId, "get_month_events", buildJsonObject {
            put("month", 3)
        })

        // Then
        assertTrue(result.contains("error"))
    }

    @Test
    fun `내 그룹 목록을 조회한다`() {
        // Given
        groupRepo.create(userId, "COUPLE", "커플그룹")
        groupRepo.create(userId, "FAMILY", "가족그룹")

        // When
        val result = tool.executeTool(userId, "get_my_groups", buildJsonObject { })

        // Then
        val json = Json.parseToJsonElement(result).jsonObject
        assertEquals(2, json["count"]!!.jsonPrimitive.int)
    }

    @Test
    fun `존재하지 않는 도구 이름은 예외를 발생시킨다`() {
        // When & Then
        assertFailsWith<IllegalArgumentException> {
            tool.executeTool(userId, "nonexistent_tool", buildJsonObject { })
        }
    }

    // ── 추가 테스트 ──

    @Test
    fun `month 파라미터가 없으면 에러를 반환한다`() {
        // When
        val result = tool.executeTool(userId, "get_month_events", buildJsonObject {
            put("year", 2026)
        })

        // Then
        val json = Json.parseToJsonElement(result).jsonObject
        assertTrue(json.containsKey("error"))
        assertTrue(json["error"]!!.jsonPrimitive.content.contains("month"))
    }

    @Test
    fun `해당 월에 일정이 없으면 빈 결과를 반환한다`() {
        // Given
        groupRepo.create(userId, "COUPLE", "테스트")

        // When
        val result = tool.executeTool(userId, "get_month_events", buildJsonObject {
            put("year", 2099)
            put("month", 1)
        })

        // Then
        val json = Json.parseToJsonElement(result).jsonObject
        val events = json["events"]!!.jsonArray
        assertTrue(events.isEmpty())
        assertEquals(0, json["count"]!!.jsonPrimitive.int)
    }

    @Test
    fun `오늘 일정 조회 시 해당 날짜만 필터링된다`() {
        // Given
        val group = groupRepo.create(userId, "COUPLE", "테스트")
        val groupId = UUID.fromString(group.id)
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.of("Asia/Seoul")).date

        // 오늘 일정 1개
        eventRepo.create(groupId, userId, CreateEventRequest(title = "오늘 일정", startAt = now))
        // 다른 날 일정 2개 (어제, 내일)
        eventRepo.create(groupId, userId, CreateEventRequest(title = "어제 일정", startAt = now - 1.days))
        eventRepo.create(groupId, userId, CreateEventRequest(title = "내일 일정", startAt = now + 1.days))

        // When
        val result = tool.executeTool(userId, "get_today_events", buildJsonObject { })

        // Then
        val json = Json.parseToJsonElement(result).jsonObject
        val events = json["events"]!!.jsonArray
        assertEquals(1, events.size)
        assertTrue(events[0].jsonObject["title"]!!.jsonPrimitive.content.contains("오늘"))
    }
}
