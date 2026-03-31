package org.bmsk.lifemash.server.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.bmsk.lifemash.social.NotificationService
import org.koin.ktor.ext.inject

fun Route.socialNotificationRoutes() {
    val notificationService by inject<NotificationService>()

    authenticate("auth-jwt") {
        route("/notifications") {
            get {
                val userId = call.currentUserId().toUUID()
                call.respond(notificationService.getNotifications(userId))
            }

            get("/unread-count") {
                val userId = call.currentUserId().toUUID()
                call.respond(mapOf("count" to notificationService.getUnreadCount(userId)))
            }

            post("/{notificationId}/read") {
                val notificationId = call.parameters["notificationId"]!!.toUUID()
                notificationService.markAsRead(notificationId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
