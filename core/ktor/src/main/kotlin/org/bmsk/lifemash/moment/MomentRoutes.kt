package org.bmsk.lifemash.moment

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.moment.MomentService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.momentRoutes() {
    val momentService by inject<MomentService>()

    authenticate("auth-jwt") {
        route("/moments") {
            post {
                val authorId = call.currentUserId().toUUID()
                val request = call.receive<CreateMomentRequest>()
                call.respond(HttpStatusCode.Created, momentService.create(authorId, request))
            }

            get("/user/{userId}") {
                val viewerId = call.currentUserId().toUUID()
                val userId = call.parameters["userId"]!!.toUUID()
                call.respond(momentService.findByUser(userId, viewerId))
            }

            delete("/{momentId}") {
                val requesterId = call.currentUserId().toUUID()
                val momentId = call.parameters["momentId"]!!.toUUID()
                momentService.delete(momentId, requesterId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
