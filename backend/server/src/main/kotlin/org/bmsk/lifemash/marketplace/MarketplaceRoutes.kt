package org.bmsk.lifemash.marketplace

import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.marketplace.SubmitBlockRequest
import org.bmsk.lifemash.config.EnvConfig
import org.bmsk.lifemash.plugins.userId
import org.koin.ktor.ext.inject

fun Route.marketplaceRoutes() {
    val service by inject<MarketplaceService>()

    route("/marketplace") {
        // 승인된 블록 목록 (인증 선택)
        get("/blocks") {
            call.respond(service.getApproved())
        }

        authenticate("auth-jwt") {
            // 블록 제출
            post("/blocks") {
                val creatorId = call.principal<JWTPrincipal>()!!.userId()
                val request = call.receive<SubmitBlockRequest>()
                val result = service.submit(creatorId, request)
                call.respond(HttpStatusCode.Created, result)
            }

            // 내가 제출한 블록 목록
            get("/blocks/mine") {
                val creatorId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(service.getMine(creatorId))
            }
        }

        // 관리자 엔드포인트
        route("/admin") {
            patch("/blocks/{id}/approve") {
                if (!call.verifyAdminToken()) return@patch
                val id = call.parameters["id"]
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val result = service.approve(id)
                if (result == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(result)
            }

            patch("/blocks/{id}/reject") {
                if (!call.verifyAdminToken()) return@patch
                val id = call.parameters["id"]
                    ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val result = service.reject(id)
                if (result == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(result)
            }
        }
    }
}

private suspend fun ApplicationCall.verifyAdminToken(): Boolean {
    val token = request.headers["X-Admin-Token"]
    val expected = EnvConfig.require("ADMIN_TOKEN")
    return if (token == expected) {
        true
    } else {
        respond(HttpStatusCode.Forbidden, mapOf("error" to "Forbidden"))
        false
    }
}
