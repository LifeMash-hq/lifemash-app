package org.bmsk.lifemash.assistant

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.assistant.AssistantService
import org.bmsk.lifemash.assistant.UserApiKeyRepository
import org.bmsk.lifemash.model.assistant.ChatRequest
import org.bmsk.lifemash.model.assistant.SaveApiKeyRequest
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.assistantRoutes() {
    val assistantService by inject<AssistantService>()
    val apiKeyRepository by inject<UserApiKeyRepository>()

    authenticate("auth-jwt") {
        route("/assistant") {
            post("/chat") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val request = call.receive<ChatRequest>()

                call.response.header(HttpHeaders.ContentType, ContentType.Text.EventStream.toString())
                call.response.header(HttpHeaders.CacheControl, "no-cache")
                call.response.header(HttpHeaders.Connection, "keep-alive")

                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    assistantService.chat(
                        userId = userId,
                        request = request,
                        emitEvent = { event ->
                            write("data: ${Json.encodeToString(event)}\n\n")
                            flush()
                        },
                    )
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
                apiKeyRepository.saveApiKey(Uuid.parse(userId), request.apiKey)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("/api-key") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                apiKeyRepository.deleteApiKey(Uuid.parse(userId))
                call.respond(HttpStatusCode.NoContent)
            }

            get("/api-key/status") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(apiKeyRepository.hasApiKey(Uuid.parse(userId)))
            }

            get("/usage") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val date = call.request.queryParameters["date"]?.let { LocalDate.parse(it) }
                call.respond(assistantService.getUsage(userId, date))
            }
        }
    }
}
