package org.bmsk.lifemash.assistant

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import org.bmsk.lifemash.assistant.tools.CalendarTool
import org.bmsk.lifemash.auth.JwtConfig
import org.bmsk.lifemash.event.EventRepository
import org.bmsk.lifemash.fake.*
import org.bmsk.lifemash.group.GroupRepository
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.ErrorResponse
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import java.util.*
import kotlin.test.*

class AssistantRoutesTest {

    private val testUserId = UUID.randomUUID().toString()
    private val testToken = JwtConfig.generateAccessToken(testUserId)

    private val fakeClaudeClient = FakeClaudeApiClient()
    private val fakeAssistantRepo = FakeAssistantRepository()
    private val fakeUsageRepo = FakeAssistantUsageRepository()
    private val fakeApiKeyRepo = FakeUserApiKeyRepository()
    private val fakeEventRepo = FakeEventRepository()
    private val fakeGroupRepo = FakeGroupRepository()
    private val fakeMarketplaceRepo = FakeMarketplaceRepository()
    private val fakeExternalToolExecutor = ExternalToolExecutor(HttpClient())

    private val testModule = module {
        single<ClaudeApiClient> { fakeClaudeClient }
        single<AssistantRepository> { fakeAssistantRepo }
        single<AssistantUsageRepository> { fakeUsageRepo }
        single<UserApiKeyRepository> { fakeApiKeyRepo }
        single<EventRepository> { fakeEventRepo }
        single<GroupRepository> { fakeGroupRepo }
        single { CalendarTool(get(), get(), fakeEventService()) }
        single { ToolRegistry(get()) }
        single<AssistantService> { AssistantServiceImpl(get(), get(), get(), get(), get(), fakeMarketplaceRepo, fakeExternalToolExecutor, serverApiKey = "test-key") }
    }

    private fun ApplicationTestBuilder.configureTestApp(): HttpClient {
        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Koin) { modules(testModule) }
        install(StatusPages) {
            exception<BadRequestException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
            }
        }
        install(Authentication) {
            jwt("auth-jwt") {
                verifier(JwtConfig.verifier)
                validate { credential ->
                    val userId = credential.payload.getClaim("userId")?.asString()
                    if (userId != null) JWTPrincipal(credential.payload) else null
                }
            }
        }
        routing {
            route("/api/v1") {
                assistantRoutes()
            }
        }
        return createClient {
            install(ContentNegotiation) { json() }
        }
    }

    @Test
    fun `인증 없이 요청 시 401을 반환한다`() = testApplication {
        val client = configureTestApp()

        // When
        val response = client.get("/api/v1/assistant/conversations")

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `대화 목록을 조회한다`() = testApplication {
        val client = configureTestApp()

        // When
        val response = client.get("/api/v1/assistant/conversations") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonArray
        assertTrue(body.isEmpty())
    }

    @Test
    fun `채팅을 전송하고 SSE 응답을 받는다`() = testApplication {
        val client = configureTestApp()

        // When
        val response = client.post("/api/v1/assistant/chat") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
            contentType(ContentType.Application.Json)
            setBody("""{"message": "안녕하세요"}""")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("done"))
    }

    @Test
    fun `대화 상세를 조회한다`() = testApplication {
        val client = configureTestApp()

        // Given — 먼저 대화 생성
        val chatResponse = client.post("/api/v1/assistant/chat") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
            contentType(ContentType.Application.Json)
            setBody("""{"message": "테스트"}""")
        }
        val convId = extractConversationId(chatResponse.bodyAsText())

        // When
        val response = client.get("/api/v1/assistant/conversations/$convId") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val detail = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertTrue(detail.containsKey("messages"))
    }

    @Test
    fun `대화를 삭제한다`() = testApplication {
        val client = configureTestApp()

        // Given
        val chatResponse = client.post("/api/v1/assistant/chat") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
            contentType(ContentType.Application.Json)
            setBody("""{"message": "삭제 테스트"}""")
        }
        val convId = extractConversationId(chatResponse.bodyAsText())

        // When
        val response = client.delete("/api/v1/assistant/conversations/$convId") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        // Then
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `API 키 상태를 조회한다`() = testApplication {
        val client = configureTestApp()

        // When
        val response = client.get("/api/v1/assistant/api-key/status") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(false, body["hasKey"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `사용량을 조회한다`() = testApplication {
        val client = configureTestApp()

        // When
        val response = client.get("/api/v1/assistant/usage") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertTrue(body.containsKey("requestCount"))
    }

    private fun extractConversationId(sseBody: String): String {
        val doneLines = sseBody.lines().filter { it.startsWith("data:") && it.contains("done") }
        for (line in doneLines) {
            val jsonStr = line.removePrefix("data: ").removePrefix("data:").trim()
            if (jsonStr == "[DONE]") continue
            val json = Json.parseToJsonElement(jsonStr).jsonObject
            if (json["type"]?.jsonPrimitive?.content == "done") {
                return json["conversationId"]!!.jsonPrimitive.content
            }
        }
        throw IllegalStateException("conversationId not found in SSE body: $sseBody")
    }
}
