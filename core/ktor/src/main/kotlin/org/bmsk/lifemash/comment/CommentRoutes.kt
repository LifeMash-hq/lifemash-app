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
        // calendar/{groupId}/... 경로의 groupId는 클라이언트 하위호환용 — 서버에선 무시
        listOf(
            "/events/{eventId}/comments",
            "/calendar/{groupId}/events/{eventId}/comments",
        ).forEach { path ->
            route(path) {
                get {
                    val userId = call.principal<JWTPrincipal>()!!.userId()
                    val eventId = call.parameters["eventId"]!!
                    call.respond(commentService.getComments(userId, eventId))
                }

                post {
                    val userId = call.principal<JWTPrincipal>()!!.userId()
                    val eventId = call.parameters["eventId"]!!
                    val request = call.receive<CreateCommentRequest>()
                    call.respond(HttpStatusCode.Created, commentService.create(userId, eventId, request))
                }

                delete("/{commentId}") {
                    val userId = call.principal<JWTPrincipal>()!!.userId()
                    val eventId = call.parameters["eventId"]!!
                    val commentId = call.parameters["commentId"]!!
                    commentService.delete(userId, eventId, commentId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
