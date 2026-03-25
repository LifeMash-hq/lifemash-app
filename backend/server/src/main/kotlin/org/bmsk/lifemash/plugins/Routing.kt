package org.bmsk.lifemash.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.assistant.assistantRoutes
import org.bmsk.lifemash.auth.authRoutes
import org.bmsk.lifemash.blocks.blocksRoutes
import org.bmsk.lifemash.marketplace.marketplaceRoutes
import org.bmsk.lifemash.comment.commentRoutes
import org.bmsk.lifemash.event.eventRoutes
import org.bmsk.lifemash.group.groupRoutes

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
        }
        route("/api/v1") {
            authRoutes()
            groupRoutes()
            eventRoutes()
            commentRoutes()
            assistantRoutes()
            blocksRoutes()
            marketplaceRoutes()
        }
    }
}
