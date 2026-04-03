package org.bmsk.lifemash.explore

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.explore.ExploreService
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
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

            get("/public-events") {
                val category = call.request.queryParameters["category"]
                val cursor = call.request.queryParameters["cursor"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                call.respond(exploreService.getPublicEvents(category, cursor, limit))
            }

            get("/heatmap") {
                val userId = call.currentUserId().toUUID()
                val year = call.request.queryParameters["year"]?.toIntOrNull()
                    ?: throw BadRequestException("year 파라미터가 필요합니다")
                val month = call.request.queryParameters["month"]?.toIntOrNull()
                    ?: throw BadRequestException("month 파라미터가 필요합니다")
                call.respond(exploreService.getHeatmap(userId, year, month))
            }

            get("/suggestions") {
                val userId = call.currentUserId().toUUID()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                call.respond(exploreService.getFollowSuggestions(userId, limit))
            }
        }
    }
}
