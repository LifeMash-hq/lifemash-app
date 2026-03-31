package org.bmsk.lifemash.assistant

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.*

class HttpClaudeApiClient(private val client: HttpClient) : ClaudeApiClient {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendMessage(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
    ): ClaudeResponse {
        val response = client.post(MESSAGES_URL) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("model", MODEL)
                put("max_tokens", MAX_TOKENS)
                put("system", systemPrompt)
                putJsonArray("messages") {
                    messages.forEach { msg ->
                        add(json.encodeToJsonElement(msg))
                    }
                }
                if (tools.isNotEmpty()) {
                    putJsonArray("tools") {
                        tools.forEach { add(it) }
                    }
                }
            }.toString())
        }

        val body = response.bodyAsText()
        if (response.status != HttpStatusCode.OK) {
            val statusCode = response.status.value
            if (statusCode == 429) throw RateLimitException()
            if (statusCode == 401) throw InvalidApiKeyException()
            throw ClaudeApiException("Claude API error: $statusCode - $body")
        }
        return json.decodeFromString<ClaudeResponse>(body)
    }

    override suspend fun sendMessageStreaming(
        apiKey: String,
        messages: List<ClaudeMessage>,
        tools: List<JsonObject>,
        systemPrompt: String,
        onDelta: suspend (String) -> Unit,
    ): ClaudeStreamResult {
        val response = client.preparePost(MESSAGES_URL) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Accept, "text/event-stream")
            setBody(buildJsonObject {
                put("model", MODEL)
                put("max_tokens", MAX_TOKENS)
                put("stream", true)
                put("system", systemPrompt)
                putJsonArray("messages") {
                    messages.forEach { msg ->
                        add(json.encodeToJsonElement(msg))
                    }
                }
                if (tools.isNotEmpty()) {
                    putJsonArray("tools") {
                        tools.forEach { add(it) }
                    }
                }
            }.toString())
        }.execute { httpResponse ->
            if (httpResponse.status != HttpStatusCode.OK) {
                val body = httpResponse.bodyAsText()
                throw ClaudeApiException("Claude API streaming error: ${httpResponse.status.value} - $body")
            }

            val channel: ByteReadChannel = httpResponse.bodyAsChannel()
            val fullText = StringBuilder()
            var inputTokens = 0
            var outputTokens = 0

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break

                if (line.startsWith("data: ")) {
                    val data = line.removePrefix("data: ")
                    if (data == "[DONE]") break

                    val event = runCatching { json.parseToJsonElement(data).jsonObject }.getOrNull()
                    if (event != null) {
                        val type = event["type"]?.jsonPrimitive?.contentOrNull
                        when (type) {
                            "content_block_delta" -> {
                                val delta = event["delta"]?.jsonObject
                                val text = delta?.get("text")?.jsonPrimitive?.contentOrNull
                                if (text != null) {
                                    fullText.append(text)
                                    onDelta(text)
                                }
                            }
                            "message_delta" -> {
                                val usage = event["usage"]?.jsonObject
                                outputTokens = usage?.get("output_tokens")?.jsonPrimitive?.intOrNull ?: outputTokens
                            }
                            "message_start" -> {
                                val message = event["message"]?.jsonObject
                                val usage = message?.get("usage")?.jsonObject
                                inputTokens = usage?.get("input_tokens")?.jsonPrimitive?.intOrNull ?: inputTokens
                            }
                        }
                    }
                }
            }

            ClaudeStreamResult(
                fullText = fullText.toString(),
                inputTokens = inputTokens,
                outputTokens = outputTokens,
            )
        }

        return response
    }

    companion object {
        private const val MESSAGES_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-sonnet-4-20250514"
        private const val MAX_TOKENS = 2048
    }
}
