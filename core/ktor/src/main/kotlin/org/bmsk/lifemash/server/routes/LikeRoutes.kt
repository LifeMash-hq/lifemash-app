package org.bmsk.lifemash.server.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.like.LikeService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.likeRoutes() {
    val likeService by inject<LikeService>()

    authenticate("auth-jwt") {
        route("/likes") {
            post("/{momentId}") {
                val userId = call.currentUserId().toUUID()
                val momentId = call.parameters["momentId"]!!.toUUID()
                likeService.like(userId, momentId)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("/{momentId}") {
                val userId = call.currentUserId().toUUID()
                val momentId = call.parameters["momentId"]!!.toUUID()
                likeService.unlike(userId, momentId)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/{momentId}/count") {
                val momentId = call.parameters["momentId"]!!.toUUID()
                call.respond(mapOf("count" to likeService.getLikeCount(momentId)))
            }
        }
    }
}
