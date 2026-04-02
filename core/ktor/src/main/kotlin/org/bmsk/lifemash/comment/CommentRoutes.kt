package org.bmsk.lifemash.comment

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.comment.CommentService
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.commentRoutes() {
    val commentService by inject<CommentService>()

    authenticate("auth-jwt") {
        route("/events/{eventId}/comments") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val eventId = call.parameters["eventId"]!!
                call.respond(commentService.getComments("", userId, eventId)) // groupId is not really used in current commentService getComments impl assuming eventId is globally unique
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val eventId = call.parameters["eventId"]!!
                val request = call.receive<CreateCommentRequest>()
                call.respond(HttpStatusCode.Created, commentService.create("", userId, eventId, request))
            }

            delete("/{commentId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val commentId = call.parameters["commentId"]!!
                commentService.delete("", userId, commentId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        
        route("/calendar/{groupId}/events/{eventId}/comments") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                call.respond(commentService.getComments(groupId, userId, eventId))
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                val request = call.receive<CreateCommentRequest>()
                call.respond(HttpStatusCode.Created, commentService.create(groupId, userId, eventId, request))
            }

            delete("/{commentId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val commentId = call.parameters["commentId"]!!
                commentService.delete(groupId, userId, commentId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
