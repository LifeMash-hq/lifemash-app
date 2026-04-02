package org.bmsk.lifemash.follow

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.follow.FollowService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.followRoutes() {
    val followService by inject<FollowService>()

    authenticate("auth-jwt") {
        route("/follow") {
            post("/{userId}") {
                val followerId = call.currentUserId().toUUID()
                val followingId = call.parameters["userId"]!!.toUUID()
                followService.follow(followerId, followingId)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("/{userId}") {
                val followerId = call.currentUserId().toUUID()
                val followingId = call.parameters["userId"]!!.toUUID()
                followService.unfollow(followerId, followingId)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/{userId}/followers") {
                val userId = call.parameters["userId"]!!.toUUID()
                call.respond(followService.getFollowers(userId))
            }

            get("/{userId}/following") {
                val userId = call.parameters["userId"]!!.toUUID()
                call.respond(followService.getFollowing(userId))
            }
        }
    }
}
