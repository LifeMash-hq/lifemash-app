package org.bmsk.lifemash.assistant

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ExternalToolExecutor(private val httpClient: HttpClient) {

    suspend fun execute(executionUrl: String, toolName: String, input: JsonObject): String {
        return try {
            val response: JsonObject = httpClient.post(executionUrl) {
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    put("tool", toolName)
                    put("input", input)
                })
            }.body()
            response.toString()
        } catch (e: Exception) {
            """{"error": "외부 앱 호출 실패: ${e.message}"}"""
        }
    }
}
