package org.bmsk.lifemash.server.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.event.EventService
import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.eventRoutes() {
    val eventService by inject<EventService>()

    authenticate("auth-jwt") {
        route("/calendar/{groupId}/events") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val year = call.request.queryParameters["year"]?.toIntOrNull()
                    ?: throw BadRequestException("year is required")
                val month = call.request.queryParameters["month"]?.toIntOrNull()
                    ?: throw BadRequestException("month is required")
                call.respond(eventService.getMonthEvents(groupId, userId, year, month))
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val request = call.receive<CreateEventRequest>()
                call.respond(HttpStatusCode.Created, eventService.create(groupId, userId, request))
            }

            patch("/{eventId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                val request = call.receive<UpdateEventRequest>()
                call.respond(eventService.update(groupId, userId, eventId, request))
            }

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
