package org.bmsk.lifemash.server

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.assistant.assistantRoutes
import org.bmsk.lifemash.auth.authRoutes
import org.bmsk.lifemash.comment.commentRoutes
import org.bmsk.lifemash.event.eventRoutes
import org.bmsk.lifemash.explore.exploreRoutes
import org.bmsk.lifemash.feed.feedRoutes
import org.bmsk.lifemash.follow.followRoutes
import org.bmsk.lifemash.group.groupRoutes
import org.bmsk.lifemash.like.likeRoutes
import org.bmsk.lifemash.memo.memoRoutes
import org.bmsk.lifemash.moment.momentRoutes
import org.bmsk.lifemash.notification.socialNotificationRoutes
import org.bmsk.lifemash.profile.profileRoutes
import org.bmsk.lifemash.upload.uploadRoutes
import org.bmsk.lifemash.user.userRoutes

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
            memoRoutes()
            assistantRoutes()
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
