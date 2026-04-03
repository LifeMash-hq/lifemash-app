package org.bmsk.lifemash.user

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.follow.FollowService
import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.model.user.UpdateSettingsRequest
import org.bmsk.lifemash.moment.MomentService
import org.bmsk.lifemash.plugins.NotFoundException
import org.bmsk.lifemash.profile.ProfileService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.bmsk.lifemash.user.UserRepository
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val profileService by inject<ProfileService>()
    val followService by inject<FollowService>()
    val momentService by inject<MomentService>()
    val userRepository by inject<UserRepository>()

    authenticate("auth-jwt") {
        route("/users") {
            // Profile
            // Settings
            get("/me/settings") {
                val userId = call.currentUserId().toUUID()
                val settings = userRepository.getSettings(userId)
                    ?: throw NotFoundException("사용자를 찾을 수 없습니다")
                call.respond(settings)
            }

            patch("/me/settings") {
                val userId = call.currentUserId().toUUID()
                val request = call.receive<UpdateSettingsRequest>()
                val updated = userRepository.updateSettings(
                    userId,
                    request.startScreen,
                    request.viewStyleSelf,
                    request.viewStyleOthers,
                    request.defaultVisibility,
                ) ?: throw NotFoundException("사용자를 찾을 수 없습니다")
                call.respond(updated)
            }

            // Profile
            get("/me/profile") {
                val userId = call.currentUserId().toUUID()
                call.respond(profileService.getProfile(userId))
            }

            patch("/me/profile") {
                val userId = call.currentUserId().toUUID()
                val request = call.receive<UpdateProfileRequest>()
                call.respond(profileService.updateProfile(userId, request))
            }

            get("/{userId}/profile") {
                val userId = call.parameters["userId"]!!.toUUID()
                val viewerId = call.currentUserId().toUUID()
                call.respond(profileService.getProfile(userId, viewerId))
            }

            // Follow
            post("/{userId}/follow") {
                val followerId = call.currentUserId().toUUID()
                val followingId = call.parameters["userId"]!!.toUUID()
                followService.follow(followerId, followingId)
                call.respond(HttpStatusCode.NoContent)
            }

            delete("/{userId}/follow") {
                val followerId = call.currentUserId().toUUID()
                val followingId = call.parameters["userId"]!!.toUUID()
                followService.unfollow(followerId, followingId)
                call.respond(HttpStatusCode.NoContent)
            }

            // Moments
            get("/{userId}/moments") {
                val viewerId = call.currentUserId().toUUID()
                val userId = call.parameters["userId"]!!.toUUID()
                call.respond(momentService.findByUser(userId, viewerId))
            }
        }
    }
}
