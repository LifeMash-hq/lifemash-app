package org.bmsk.lifemash.fake

import kotlinx.serialization.json.JsonObject
import org.bmsk.lifemash.assistant.*

class FakeClaudeApiClient : ClaudeApiClient {
    private val responseQueue = ArrayDeque<ClaudeResponse>()
    var nextStreamResult: ClaudeStreamResult = ClaudeStreamResult("Hello!", 10, 5)
    var lastApiKey: String? = null
    var sendMessageCallCount = 0

    private val defaultResponse = ClaudeResponse(
        id = "msg_fake",
        content = listOf(ContentBlock(type = "text", text = "OK")),
        stop_reason = "end_turn",
        usage = ClaudeUsage(input_tokens = 10, output_tokens = 5),
    )

    fun enqueueSendMessageResponse(response: ClaudeResponse) {
        responseQueue.addLast(response)
    }

    override suspend fun sendMessage(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
    ): ClaudeResponse {
        lastApiKey = apiKey
        sendMessageCallCount++
        return responseQueue.removeFirstOrNull() ?: defaultResponse
    }

    override suspend fun sendMessageStreaming(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
        onDelta: suspend (String) -> Unit,
    ): ClaudeStreamResult {
        lastApiKey = apiKey
        onDelta(nextStreamResult.fullText)
        return nextStreamResult
    }
}
