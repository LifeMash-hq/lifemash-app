package org.bmsk.lifemash.assistant

import kotlinx.serialization.json.JsonObject

class StubClaudeApiClient : ClaudeApiClient {
    override suspend fun sendMessage(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
    ): ClaudeResponse = ClaudeResponse(
        id = "demo-response-id",
        content = listOf(ContentBlock(type = "text", text = "데모 모드 응답입니다.")),
        stop_reason = "end_turn",
        usage = ClaudeUsage(input_tokens = 0, output_tokens = 0),
    )

    override suspend fun sendMessageStreaming(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
        onDelta: suspend (String) -> Unit,
    ): ClaudeStreamResult {
        onDelta("데모 모드 응답입니다.")
        return ClaudeStreamResult(
            fullText = "데모 모드 응답입니다.",
            inputTokens = 0,
            outputTokens = 0,
        )
    }
}
