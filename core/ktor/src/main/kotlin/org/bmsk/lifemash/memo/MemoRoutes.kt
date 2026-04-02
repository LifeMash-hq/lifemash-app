package org.bmsk.lifemash.memo

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.memo.MemoService
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.SyncChecklistRequest
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.memoRoutes() {
    val memoService by inject<MemoService>()

    authenticate("auth-jwt") {
        route("/calendar/{groupId}/memos") {
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                call.respond(memoService.getGroupMemos(groupId, userId))
            }

            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val request = call.receive<CreateMemoRequest>()
                call.respond(HttpStatusCode.Created, memoService.create(groupId, userId, request))
            }

            get("/search") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val query = call.request.queryParameters["q"]
                    ?: throw BadRequestException("q is required")
                call.respond(memoService.search(groupId, userId, query))
            }

            get("/{memoId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val memoId = call.parameters["memoId"]!!
                call.respond(memoService.getMemo(groupId, memoId, userId))
            }

            patch("/{memoId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val memoId = call.parameters["memoId"]!!
                val request = call.receive<UpdateMemoRequest>()
                call.respond(memoService.update(groupId, memoId, userId, request))
            }

            delete("/{memoId}") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val memoId = call.parameters["memoId"]!!
                memoService.delete(groupId, memoId, userId)
                call.respond(HttpStatusCode.NoContent)
            }

            put("/{memoId}/checklist") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val groupId = call.parameters["groupId"]!!
                val memoId = call.parameters["memoId"]!!
                val request = call.receive<SyncChecklistRequest>()
                call.respond(memoService.syncChecklist(groupId, memoId, userId, request))
            }
        }
    }
}
