package org.bmsk.lifemash.event

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.userId
import org.koin.ktor.ext.inject

/**
 * 일정(Event) API 라우트.
 * 모든 엔드포인트가 JWT 인증 필요.
 *
 * 엔드포인트 목록:
 *   GET    /api/v1/calendar/{groupId}/events?year=2026&month=3 → 월별 일정 조회
 *   POST   /api/v1/calendar/{groupId}/events                  → 일정 생성
 *   PATCH  /api/v1/calendar/{groupId}/events/{eventId}        → 일정 부분 수정
 *   DELETE /api/v1/calendar/{groupId}/events/{eventId}        → 일정 삭제
 */
fun Route.eventRoutes() {
    val eventService by inject<EventService>()

    authenticate("auth-jwt") {
        route("/calendar/{groupId}/events") {
            // 월별 일정 조회 — year, month를 쿼리 파라미터(URL ?뒤)로 받음
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val year = call.request.queryParameters["year"]?.toIntOrNull()
                    ?: throw BadRequestException("year is required")
                val month = call.request.queryParameters["month"]?.toIntOrNull()
                    ?: throw BadRequestException("month is required")
                call.respond(eventService.getMonthEvents(groupId, userId, year, month))
            }

            // 일정 생성 — 201 Created 상태코드로 응답
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val request = call.receive<CreateEventRequest>()
                call.respond(HttpStatusCode.Created, eventService.create(groupId, userId, request))
            }

            // 일정 부분 수정 — PATCH: 변경할 필드만 보내는 부분 업데이트
            patch("/{eventId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                val request = call.receive<UpdateEventRequest>()
                call.respond(eventService.update(groupId, userId, eventId, request))
            }

            // 일정 삭제
            delete("/{eventId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                eventService.delete(groupId, userId, eventId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
