package org.bmsk.lifemash.server.routes

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.feed.FeedService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.feedRoutes() {
    val feedService by inject<FeedService>()

    authenticate("auth-jwt") {
        route("/feed") {
            get {
                val userId = call.currentUserId().toUUID()
                val cursor = call.request.queryParameters["cursor"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                call.respond(feedService.getFeed(userId, cursor, limit))
            }

            get("/trending") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                call.respond(feedService.getTrending(limit))
            }
        }
    }
}
