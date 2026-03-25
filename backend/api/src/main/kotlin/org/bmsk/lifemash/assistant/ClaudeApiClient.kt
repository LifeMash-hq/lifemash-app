package org.bmsk.lifemash.assistant

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

interface ClaudeApiClient {
    suspend fun sendMessage(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
    ): ClaudeResponse

    suspend fun sendMessageStreaming(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject> = emptyList(),
        systemPrompt: String,
        onDelta: suspend (String) -> Unit,
    ): ClaudeStreamResult
}

// ── Claude API 데이터 모델 ──

@Serializable
data class ClaudeMessage(
    val role: String,
    val content: JsonElement,
)

@Serializable
data class ClaudeResponse(
    val id: String,
    val content: List<ContentBlock>,
    @Serializable(with = StopReasonSerializer::class)
    val stop_reason: String? = null,
    val usage: ClaudeUsage? = null,
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String? = null,
    val id: String? = null,
    val name: String? = null,
    val input: JsonObject? = null,
)

@Serializable
data class ClaudeUsage(
    val input_tokens: Int = 0,
    val output_tokens: Int = 0,
)

data class ClaudeStreamResult(
    val fullText: String,
    val inputTokens: Int,
    val outputTokens: Int,
)

// ── Claude API 에러 ──
class RateLimitException : RuntimeException("Rate limited by Claude API")
class InvalidApiKeyException : RuntimeException("Invalid API key")
class ClaudeApiException(message: String) : RuntimeException(message)

private object StopReasonSerializer : kotlinx.serialization.KSerializer<String?> {
    override val descriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("StopReason", kotlinx.serialization.descriptors.PrimitiveKind.STRING)
    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: String?) {
        if (value != null) encoder.encodeString(value) else encoder.encodeString("")
    }
    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): String? {
        val s = decoder.decodeString()
        return s.ifEmpty { null }
    }
}
