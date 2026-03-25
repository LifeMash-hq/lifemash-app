package org.bmsk.lifemash.assistant.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.SseEvent

internal class SseClient(private val client: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun streamChat(request: ChatRequest, onEvent: suspend (SseEvent) -> Unit) {
        client.preparePost("/api/v1/assistant/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
            header(HttpHeaders.Accept, "text/event-stream")
        }.execute { response ->
            if (!response.status.isSuccess()) {
                val body = runCatching { response.bodyAsText() }.getOrDefault("")
                throw AssistantApiException(response.status.value, body)
            }

            val channel: ByteReadChannel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val data = line.removePrefix("data: ")
                    if (data == "[DONE]") break
                    val event = runCatching { json.decodeFromString<SseEvent>(data) }.getOrNull()
                    if (event != null) {
                        onEvent(event)
                    }
                }
            }
        }
    }
}

internal class AssistantApiException(
    val statusCode: Int,
    val body: String,
) : Exception("Assistant API error $statusCode: $body")
