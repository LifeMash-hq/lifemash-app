package org.bmsk.lifemash.explore

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.explore.ExploreService
import org.bmsk.lifemash.plugins.BadRequestException
import org.koin.ktor.ext.inject

fun Route.exploreRoutes() {
    val exploreService by inject<ExploreService>()

    authenticate("auth-jwt") {
        route("/explore") {
            get("/users") {
                val query = call.request.queryParameters["q"]
                    ?: throw BadRequestException("검색어(q)가 필요합니다")
                call.respond(exploreService.searchUsers(query))
            }

            get("/events") {
                val query = call.request.queryParameters["q"]
                    ?: throw BadRequestException("검색어(q)가 필요합니다")
                call.respond(exploreService.searchEvents(query))
            }

            get("/trending") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                call.respond(exploreService.getTrending(limit))
            }
        }
    }
}
