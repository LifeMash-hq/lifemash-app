package org.bmsk.lifemash.assistant

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.bmsk.lifemash.assistant.tools.CalendarTool
import org.bmsk.lifemash.fake.FakeEventRepository
import org.bmsk.lifemash.fake.FakeGroupRepository
import org.bmsk.lifemash.fake.fakeEventService
import java.util.*
import kotlin.test.*

class ToolRegistryTest {

    private lateinit var registry: ToolRegistry

    @BeforeTest
    fun setUp() {
        val calendarTool = CalendarTool(FakeEventRepository(), FakeGroupRepository(), fakeEventService())
        registry = ToolRegistry(calendarTool)
    }

    @Test
    fun `존재하지 않는 도구 실행 시 에러 결과를 반환한다`() {
        // When
        val result = registry.executeTool(UUID.randomUUID(), "unknown_tool", JsonObject(emptyMap()))

        // Then
        assertTrue(result.isError)
        assertTrue(result.content.contains("Unknown tool"))
    }

    @Test
    fun `도구 실행 중 예외 발생 시 에러 결과로 감싼다`() {
        // Given — get_month_events에 필수 파라미터 없이 호출
        val input = buildJsonObject { } // year, month 없음

        // When
        val result = registry.executeTool(UUID.randomUUID(), "get_month_events", input)

        // Then — 에러가 아닌 "error" 필드가 content에 포함된 결과
        assertTrue(result.content.contains("error"))
    }
}
