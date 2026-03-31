package org.bmsk.lifemash.marketplace

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ToolManifestFetcher(private val httpClient: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    /** manifestUrl에서 도구 정의 JSON을 가져와 문자열로 반환. 실패 시 null 반환 (승인은 계속 진행). */
    suspend fun fetch(manifestUrl: String): String? {
        return try {
            val response: JsonObject = httpClient.get(manifestUrl).body()
            response["tools"]?.let { json.encodeToString(it) }
        } catch (e: Exception) {
            null
        }
    }
}
