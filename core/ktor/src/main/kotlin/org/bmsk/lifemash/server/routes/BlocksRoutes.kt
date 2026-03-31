package org.bmsk.lifemash.server.routes

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.blocks.BlocksService
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.blocksRoutes() {
    val blocksService by inject<BlocksService>()

    authenticate("auth-jwt") {
        route("/blocks") {
            get("/today") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(blocksService.getTodayData(userId))
            }
        }
    }
}
