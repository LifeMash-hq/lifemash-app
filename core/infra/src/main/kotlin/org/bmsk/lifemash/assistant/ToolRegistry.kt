package org.bmsk.lifemash.assistant

import kotlinx.serialization.json.JsonObject
import org.bmsk.lifemash.assistant.tools.CalendarTool
import kotlin.uuid.Uuid

/**
 * AI 도구 레지스트리 — 네이티브 내장 도구를 관리하고 실행을 라우팅.
 *
 * 뉴스 등 블록 마켓플레이스 도구는 ExternalToolExecutor 경로로 실행되므로 여기에 등록하지 않는다.
 */
class ToolRegistry(
    private val calendarTool: CalendarTool,
) {

    fun getAllToolDefinitions(): List<JsonObject> = calendarTool.getToolDefinitions()

    fun executeTool(userId: Uuid, toolName: String, input: JsonObject): ToolResult {
        return try {
            if (toolName in CALENDAR_TOOLS) {
                ToolResult(calendarTool.executeTool(userId, toolName, input))
            } else {
                ToolResult("""{"error": "Unknown tool: $toolName"}""", isError = true)
            }
        } catch (e: Exception) {
            ToolResult("""{"error": "${e.message}"}""", isError = true)
        }
    }

    fun isKnownTool(toolName: String): Boolean = toolName in CALENDAR_TOOLS

    companion object {
        private val CALENDAR_TOOLS = setOf("get_today_events", "get_month_events", "get_my_groups", "create_event")
    }
}

/** 도구 실행 결과 — content(JSON 문자열) + 에러 여부 */
data class ToolResult(val content: String, val isError: Boolean = false)
