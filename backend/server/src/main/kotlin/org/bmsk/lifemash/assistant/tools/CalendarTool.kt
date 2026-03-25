package org.bmsk.lifemash.assistant.tools

import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.*
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.GroupDto
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.group.GroupRepository
import java.util.*

/**
 * AI 어시스턴트가 사용하는 캘린더 도구(Tool).
 *
 * Claude API의 "Tool Use" 기능:
 * AI가 사용자의 질문에 답하기 위해 직접 함수를 호출할 수 있다.
 * 예: "오늘 일정 알려줘" → AI가 get_today_events 도구를 호출 → 결과를 받아 답변 생성
 *
 * 도구 정의(getToolDefinitions)와 실행(executeTool)을 제공한다.
 */
class CalendarTool(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val eventService: EventService,
) {

    /** AI에게 제공할 도구 목록 정의 (Claude API의 tool 스펙 형식) */
    fun getToolDefinitions(): List<JsonObject> = listOf(
        buildJsonObject {
            put("name", "get_today_events")
            put("description", "사용자의 오늘 모든 그룹 일정을 조회합니다.")
            putJsonObject("input_schema") {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("timezone") {
                        put("type", "string")
                        put("description", "타임존 (기본값: Asia/Seoul)")
                    }
                }
                putJsonArray("required") {}
            }
        },
        buildJsonObject {
            put("name", "get_month_events")
            put("description", "특정 월의 사용자 모든 그룹 일정을 조회합니다.")
            putJsonObject("input_schema") {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("year") {
                        put("type", "integer")
                        put("description", "연도")
                    }
                    putJsonObject("month") {
                        put("type", "integer")
                        put("description", "월 (1-12)")
                    }
                    putJsonObject("timezone") {
                        put("type", "string")
                        put("description", "타임존 (기본값: Asia/Seoul)")
                    }
                }
                putJsonArray("required") {
                    add("year")
                    add("month")
                }
            }
        },
        buildJsonObject {
            put("name", "get_my_groups")
            put("description", "사용자가 속한 그룹 목록을 조회합니다.")
            putJsonObject("input_schema") {
                put("type", "object")
                putJsonObject("properties") {}
                putJsonArray("required") {}
            }
        },
        buildJsonObject {
            put("name", "create_event")
            put("description", "사용자의 그룹 캘린더에 새 일정을 생성합니다. groupId는 get_my_groups로 먼저 조회해야 합니다.")
            putJsonObject("input_schema") {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("groupId") {
                        put("type", "string")
                        put("description", "일정을 추가할 그룹 ID (get_my_groups로 조회)")
                    }
                    putJsonObject("title") {
                        put("type", "string")
                        put("description", "일정 제목")
                    }
                    putJsonObject("description") {
                        put("type", "string")
                        put("description", "일정 설명 (선택)")
                    }
                    putJsonObject("startAt") {
                        put("type", "string")
                        put("description", "시작 시각 (ISO 8601, 예: 2026-03-20T09:00:00Z)")
                    }
                    putJsonObject("endAt") {
                        put("type", "string")
                        put("description", "종료 시각 (ISO 8601, 선택)")
                    }
                    putJsonObject("isAllDay") {
                        put("type", "boolean")
                        put("description", "종일 일정 여부 (기본값: false)")
                    }
                    putJsonObject("color") {
                        put("type", "string")
                        put("description", "색상 (#RRGGBB, 선택)")
                    }
                }
                putJsonArray("required") {
                    add("groupId")
                    add("title")
                    add("startAt")
                }
            }
        },
    )

    /** AI가 호출한 도구를 실행하고 결과(JSON 문자열)를 반환 */
    fun executeTool(userId: UUID, toolName: String, input: JsonObject): String {
        return when (toolName) {
            "get_today_events" -> getTodayEvents(userId, input)
            "get_month_events" -> getMonthEvents(userId, input)
            "get_my_groups" -> getMyGroups(userId)
            "create_event" -> createEvent(userId, input)
            else -> throw IllegalArgumentException("Unknown tool: $toolName")
        }
    }

    private fun getTodayEvents(userId: UUID, input: JsonObject): String {
        val tz = input["timezone"]?.jsonPrimitive?.contentOrNull ?: "Asia/Seoul"
        val today = Clock.System.now().toLocalDateTime(TimeZone.of(tz)).date
        return getEventsForMonth(userId, today.year, today.monthNumber, today)
    }

    private fun getMonthEvents(userId: UUID, input: JsonObject): String {
        val year = input["year"]?.jsonPrimitive?.int ?: return """{"error": "year is required"}"""
        val month = input["month"]?.jsonPrimitive?.int ?: return """{"error": "month is required"}"""
        return getEventsForMonth(userId, year, month, filterDate = null)
    }

    private fun getEventsForMonth(userId: UUID, year: Int, month: Int, filterDate: LocalDate?): String {
        val groups = groupRepository.findByUserId(userId)
        if (groups.isEmpty()) return """{"events": [], "message": "가입된 그룹이 없습니다."}"""

        val allEvents = groups.flatMap { group ->
            val events = eventRepository.getMonthEvents(UUID.fromString(group.id), year, month)
            events.map { event ->
                buildJsonObject {
                    put("id", event.id)
                    put("title", event.title)
                    put("description", event.description)
                    put("startAt", event.startAt.toString())
                    put("endAt", event.endAt?.toString())
                    put("isAllDay", event.isAllDay)
                    put("color", event.color)
                    put("groupName", group.name)
                }
            }
        }.let { events ->
            if (filterDate != null) {
                events.filter { event ->
                    val startStr = event["startAt"]?.jsonPrimitive?.contentOrNull ?: return@filter false
                    val startInstant = Instant.parse(startStr)
                    val eventDate = startInstant.toLocalDateTime(TimeZone.of("Asia/Seoul")).date
                    eventDate == filterDate
                }
            } else {
                events
            }
        }

        return buildJsonObject {
            putJsonArray("events") { allEvents.forEach { add(it) } }
            put("count", allEvents.size)
        }.toString()
    }

    private fun createEvent(userId: UUID, input: JsonObject): String {
        val groupId = input["groupId"]?.jsonPrimitive?.contentOrNull
            ?: return """{"error": "groupId is required"}"""
        val title = input["title"]?.jsonPrimitive?.contentOrNull
            ?: return """{"error": "title is required"}"""
        val startAtStr = input["startAt"]?.jsonPrimitive?.contentOrNull
            ?: return """{"error": "startAt is required"}"""

        val startAt = try { Instant.parse(startAtStr) } catch (e: Exception) {
            return """{"error": "startAt 형식이 잘못되었습니다. ISO 8601 형식을 사용하세요 (예: 2026-03-20T09:00:00Z)"}"""
        }
        val endAt = input["endAt"]?.jsonPrimitive?.contentOrNull?.let {
            try { Instant.parse(it) } catch (e: Exception) { null }
        }

        val request = CreateEventRequest(
            title = title,
            description = input["description"]?.jsonPrimitive?.contentOrNull,
            startAt = startAt,
            endAt = endAt,
            isAllDay = input["isAllDay"]?.jsonPrimitive?.booleanOrNull ?: false,
            color = input["color"]?.jsonPrimitive?.contentOrNull,
        )

        val event = eventService.create(groupId, userId.toString(), request)
        return buildJsonObject {
            put("id", event.id)
            put("title", event.title)
            put("startAt", event.startAt.toString())
            event.endAt?.let { put("endAt", it.toString()) }
            put("isAllDay", event.isAllDay)
            put("message", "일정이 성공적으로 생성되었습니다.")
        }.toString()
    }

    private fun getMyGroups(userId: UUID): String {
        val groups = groupRepository.findByUserId(userId)
        return buildJsonObject {
            putJsonArray("groups") {
                groups.forEach { group ->
                    add(buildJsonObject {
                        put("id", group.id)
                        put("name", group.name)
                        put("type", group.type)
                        put("memberCount", group.members.size)
                    })
                }
            }
            put("count", groups.size)
        }.toString()
    }
}
