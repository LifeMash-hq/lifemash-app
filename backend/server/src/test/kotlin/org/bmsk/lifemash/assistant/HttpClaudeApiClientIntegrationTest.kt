package org.bmsk.lifemash.assistant

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.test.*

/**
 * 실제 Claude API 연동 테스트.
 * CLAUDE_API_KEY 환경변수가 있을 때만 실행된다.
 * rate limit(분당 5회) 회피를 위해 순차 실행 + 딜레이.
 */
class HttpClaudeApiClientIntegrationTest {

    private val apiKey = System.getenv("CLAUDE_API_KEY")

    private val client by lazy {
        HttpClaudeApiClient(
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        )
    }

    @Test
    fun `실제 Claude API 연동 검증`() = runBlocking {
        if (apiKey.isNullOrBlank()) {
            println("CLAUDE_API_KEY not set — skipping integration test")
            return@runBlocking
        }

        // 1. 텍스트 응답
        val textResponse = client.sendMessage(
            apiKey = apiKey,
            messages = listOf(ClaudeMessage(role = "user", content = JsonPrimitive("1+1=? 숫자만 답해"))),
            tools = emptyList(),
            systemPrompt = "간결하게 답하세요.",
        )
        assertNotNull(textResponse.id)
        assertTrue(textResponse.content.isNotEmpty())
        assertEquals("text", textResponse.content[0].type)
        assertNotNull(textResponse.content[0].text)
        assertEquals("end_turn", textResponse.stop_reason)
        assertNotNull(textResponse.usage)
        assertTrue(textResponse.usage!!.input_tokens > 0)
        assertTrue(textResponse.usage!!.output_tokens > 0)
        println("✓ sendMessage 텍스트 응답: ${textResponse.content[0].text}")

        delay(2000) // rate limit 회피

        // 2. tool_use 응답
        val tools = listOf(
            buildJsonObject {
                put("name", "get_weather")
                put("description", "현재 날씨를 조회합니다.")
                putJsonObject("input_schema") {
                    put("type", "object")
                    putJsonObject("properties") {
                        putJsonObject("city") {
                            put("type", "string")
                            put("description", "도시 이름")
                        }
                    }
                    putJsonArray("required") { add(JsonPrimitive("city")) }
                }
            }
        )
        val toolResponse = client.sendMessage(
            apiKey = apiKey,
            messages = listOf(ClaudeMessage(role = "user", content = JsonPrimitive("서울 날씨 알려줘"))),
            tools = tools,
            systemPrompt = "도구를 사용해서 답하세요.",
        )
        assertNotNull(toolResponse.id)
        val toolUseBlock = toolResponse.content.find { it.type == "tool_use" }
        assertNotNull(toolUseBlock, "tool_use 블록이 있어야 한다")
        assertEquals("get_weather", toolUseBlock.name)
        assertNotNull(toolUseBlock.id)
        assertNotNull(toolUseBlock.input)
        assertEquals("tool_use", toolResponse.stop_reason)
        println("✓ sendMessage tool_use 응답: tool=${toolUseBlock.name}, input=${toolUseBlock.input}")

        delay(2000)

        // 3. 스트리밍 응답
        val deltas = mutableListOf<String>()
        val streamResult = client.sendMessageStreaming(
            apiKey = apiKey,
            messages = listOf(ClaudeMessage(role = "user", content = JsonPrimitive("안녕이라고만 답해"))),
            systemPrompt = "간결하게 답하세요.",
        ) { delta ->
            deltas.add(delta)
        }
        assertTrue(streamResult.fullText.isNotBlank(), "응답 텍스트가 비어있지 않아야 한다")
        assertTrue(deltas.isNotEmpty(), "스트리밍 델타가 하나 이상 있어야 한다")
        assertTrue(streamResult.inputTokens > 0)
        assertTrue(streamResult.outputTokens > 0)
        println("✓ sendMessageStreaming 응답: ${streamResult.fullText} (deltas=${deltas.size}개)")

        delay(2000)

        // 4. 잘못된 API 키 → InvalidApiKeyException
        assertFailsWith<InvalidApiKeyException> {
            client.sendMessage(
                apiKey = "sk-ant-invalid-key",
                messages = listOf(ClaudeMessage(role = "user", content = JsonPrimitive("테스트"))),
                tools = emptyList(),
                systemPrompt = "",
            )
        }
        println("✓ 잘못된 API 키 → InvalidApiKeyException 정상 발생")
    }
}
