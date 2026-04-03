@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)
package org.bmsk.lifemash.event

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.bmsk.lifemash.auth.JwtConfig
import org.bmsk.lifemash.model.calendar.EventDetailDto
import org.bmsk.lifemash.model.calendar.ToggleJoinResponse
import org.bmsk.lifemash.server.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.*
import kotlin.uuid.Uuid
import kotlin.time.Clock

class EventRoutesTest {
    
    @Test
    fun `GET events eventId - 성공 시 200과 Dto를 반환한다`() = testApplication {
        val testEventId = Uuid.random().toString()
        val testUserId = Uuid.random().toString()
        val testToken = JwtConfig.generateAccessToken(testUserId)

        // Mock Service
        val mockService = object : EventService by EventServiceStub() {
            override fun getEventDetail(userId: String, eventId: String): EventDetailDto {
                return EventDetailDto(
                    id = eventId,
                    groupId = Uuid.random().toString(),
                    title = "Test Event",
                    description = null,
                    startAt = Clock.System.now(),
                    endAt = null,
                    isAllDay = false,
                    location = null,
                    imageEmoji = null,
                    authorNickname = "Tester",
                    attendees = emptyList(),
                    comments = emptyList(),
                    isJoined = false
                )
            }
        }

        application {
            install(Koin) {
                modules(module { single<EventService> { mockService } })
            }
            configureSerialization()
            configureAuthentication()
            routing {
                route("/api/v1") {
                    eventRoutes()
                }
            }
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.get("/api/v1/events/$testEventId") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `POST events eventId join - 성공 시 200과 ToggleJoinResponse를 반환한다`() = testApplication {
        val testEventId = Uuid.random().toString()
        val testUserId = Uuid.random().toString()
        val testToken = JwtConfig.generateAccessToken(testUserId)

        val mockService = object : EventService by EventServiceStub() {
            override fun toggleJoin(userId: String, eventId: String): Boolean = true
        }

        application {
            install(Koin) {
                modules(module { single<EventService> { mockService } })
            }
            configureSerialization()
            configureAuthentication()
            routing {
                route("/api/v1") {
                    eventRoutes()
                }
            }
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/api/v1/events/$testEventId/join") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}

// Helper Stub to avoid implementing all methods
open class EventServiceStub : EventService {
    override fun getMonthEvents(groupId: String, userId: String, year: Int, month: Int): List<org.bmsk.lifemash.model.calendar.EventDto> = emptyList()
    override fun create(groupId: String, userId: String, request: org.bmsk.lifemash.model.calendar.CreateEventRequest): org.bmsk.lifemash.model.calendar.EventDto {
        val now = Clock.System.now()
        return org.bmsk.lifemash.model.calendar.EventDto(
            id = Uuid.random().toString(),
            groupId = groupId,
            authorId = userId,
            title = request.title,
            startAt = request.startAt,
            createdAt = now,
            updatedAt = now,
        )
    }
    override fun update(groupId: String, userId: String, eventId: String, request: org.bmsk.lifemash.model.calendar.UpdateEventRequest): org.bmsk.lifemash.model.calendar.EventDto {
        val now = Clock.System.now()
        return org.bmsk.lifemash.model.calendar.EventDto(
            id = eventId,
            groupId = groupId,
            authorId = userId,
            title = request.title ?: "",
            startAt = request.startAt ?: now,
            createdAt = now,
            updatedAt = now,
        )
    }
    override fun delete(groupId: String, userId: String, eventId: String) {}
    override fun getEventDetail(userId: String, eventId: String): EventDetailDto = EventDetailDto(
        id = eventId,
        groupId = "",
        title = "",
        description = null,
        startAt = Clock.System.now(),
        endAt = null,
        isAllDay = false,
        location = null,
        imageEmoji = null,
        authorNickname = null,
        attendees = emptyList(),
        comments = emptyList(),
        isJoined = false,
    )
    override fun toggleJoin(userId: String, eventId: String): Boolean = false
}
