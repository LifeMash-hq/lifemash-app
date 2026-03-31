package org.bmsk.lifemash.server

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.server.routes.*

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/health") {
            call.respondText("OK")
        }
        route("/api/v1") {
            authRoutes()
            groupRoutes()
            eventRoutes()
            commentRoutes()
            assistantRoutes()
            blocksRoutes()
            marketplaceRoutes()
            followRoutes()
            momentRoutes()
            profileRoutes()
            userRoutes()
            feedRoutes()
            likeRoutes()
            socialNotificationRoutes()
            exploreRoutes()
            uploadRoutes()
        }
    }
}
