package org.bmsk.lifemash.comment

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.calendar.CreateCommentRequest
import org.bmsk.lifemash.plugins.userId
import org.koin.ktor.ext.inject

/**
 * 댓글(Comment) API 라우트.
 *
 * 엔드포인트:
 *   GET    /api/v1/calendar/{groupId}/events/{eventId}/comments              → 댓글 목록 조회
 *   POST   /api/v1/calendar/{groupId}/events/{eventId}/comments              → 댓글 생성
 *   DELETE /api/v1/calendar/{groupId}/events/{eventId}/comments/{commentId}  → 댓글 삭제
 *
 * RESTful 중첩 리소스 패턴:
 *   그룹 > 일정 > 댓글 순으로 URL이 계층적으로 구성됨.
 */
fun Route.commentRoutes() {
    val commentService by inject<CommentService>()

    authenticate("auth-jwt") {
        route("/calendar/{groupId}/events/{eventId}/comments") {
            // 특정 일정의 댓글 목록 조회
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                call.respond(commentService.getComments(groupId, userId, eventId))
            }

            // 댓글 생성 — 201 Created 응답
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val eventId = call.parameters["eventId"]!!
                val request = call.receive<CreateCommentRequest>()
                call.respond(HttpStatusCode.Created, commentService.create(groupId, userId, eventId, request))
            }

            // 댓글 삭제 — 작성자 본인만 가능
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
