package org.bmsk.lifemash.assistant

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.SaveApiKeyRequest
import org.bmsk.lifemash.model.assistant.SseEvent
import org.bmsk.lifemash.plugins.userId
import org.koin.ktor.ext.inject

/**
 * AI 어시스턴트 API 라우트.
 *
 * 엔드포인트:
 *   POST   /api/v1/assistant/chat                → AI 채팅 (SSE 스트리밍 응답)
 *   GET    /api/v1/assistant/conversations        → 대화 목록 (페이징)
 *   GET    /api/v1/assistant/conversations/{id}   → 대화 상세 (메시지 포함)
 *   DELETE /api/v1/assistant/conversations/{id}   → 대화 삭제
 *   PUT    /api/v1/assistant/api-key              → 사용자 API 키 등록
 *   DELETE /api/v1/assistant/api-key              → 사용자 API 키 삭제
 *   GET    /api/v1/assistant/api-key/status       → API 키 등록 여부 확인
 *   GET    /api/v1/assistant/usage                → 일일 사용량 조회
 */
fun Route.assistantRoutes() {
    val assistantService by inject<AssistantService>()
    val apiKeyRepository by inject<UserApiKeyRepository>()

    authenticate("auth-jwt") {
        route("/assistant") {
            // AI 채팅 — SSE(Server-Sent Events) 형식으로 실시간 응답 스트리밍
            post("/chat") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val request = call.receive<ChatRequest>()

                call.response.header(HttpHeaders.ContentType, ContentType.Text.EventStream.toString())
                call.response.header(HttpHeaders.CacheControl, "no-cache")
                call.response.header(HttpHeaders.Connection, "keep-alive")

                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    assistantService.chat(userId, request) { event ->
                        write("data: ${Json.encodeToString(event)}\n\n")
                        flush()
                    }
                    write("data: [DONE]\n\n")
                    flush()
                }
            }

            get("/conversations") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0
                call.respond(assistantService.getConversations(userId, limit, offset))
            }

            get("/conversations/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val conversationId = call.parameters["id"]!!
                call.respond(assistantService.getConversationDetail(userId, conversationId))
            }

            delete("/conversations/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val conversationId = call.parameters["id"]!!
                assistantService.deleteConversation(userId, conversationId)
                call.respond(HttpStatusCode.NoContent)
            }

            put("/api-key") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val request = call.receive<SaveApiKeyRequest>()
                apiKeyRepository.saveApiKey(java.util.UUID.fromString(userId), request.apiKey)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("/api-key") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                apiKeyRepository.deleteApiKey(java.util.UUID.fromString(userId))
                call.respond(HttpStatusCode.NoContent)
            }

            get("/api-key/status") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(apiKeyRepository.hasApiKey(java.util.UUID.fromString(userId)))
            }

            get("/usage") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val date = call.request.queryParameters["date"]?.let { LocalDate.parse(it) }
                call.respond(assistantService.getUsage(userId, date))
            }
        }
    }
}
