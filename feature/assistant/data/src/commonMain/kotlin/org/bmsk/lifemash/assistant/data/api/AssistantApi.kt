package org.bmsk.lifemash.assistant.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.assistant.data.api.dto.ApiKeyStatusResponseDto
import org.bmsk.lifemash.assistant.data.api.dto.ConversationDetailDto
import org.bmsk.lifemash.assistant.data.api.dto.ConversationDto
import org.bmsk.lifemash.assistant.data.api.dto.SaveApiKeyRequestDto
import org.bmsk.lifemash.assistant.data.api.dto.UsageResponseDto

internal class AssistantApi(private val client: HttpClient) {

    private val base = "/api/v1/assistant"

    suspend fun getConversations(limit: Int, offset: Int): List<ConversationDto> =
        client.get("$base/conversations") {
            url.parameters.append("limit", limit.toString())
            url.parameters.append("offset", offset.toString())
        }.body()

    suspend fun getConversation(id: String): ConversationDetailDto =
        client.get("$base/conversations/$id").body()

    suspend fun deleteConversation(id: String): Unit =
        client.delete("$base/conversations/$id").body()

    suspend fun saveApiKey(apiKey: String): Unit =
        client.put("$base/api-key") {
            contentType(ContentType.Application.Json)
            setBody(SaveApiKeyRequestDto(apiKey))
        }.body()

    suspend fun deleteApiKey(): Unit =
        client.delete("$base/api-key").body()

    suspend fun getApiKeyStatus(): ApiKeyStatusResponseDto =
        client.get("$base/api-key/status").body()

    suspend fun getUsage(date: String?): UsageResponseDto =
        client.get("$base/usage") {
            date?.let { url.parameters.append("date", it) }
        }.body()
}
