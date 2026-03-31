package org.bmsk.lifemash.server.routes

import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.profile.ProfileService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.profileRoutes() {
    val profileService by inject<ProfileService>()

    authenticate("auth-jwt") {
        route("/profile") {
            get {
                val userId = call.currentUserId().toUUID()
                call.respond(profileService.getProfile(userId))
            }

            patch {
                val userId = call.currentUserId().toUUID()
                val request = call.receive<UpdateProfileRequest>()
                call.respond(profileService.updateProfile(userId, request))
            }

            get("/{userId}") {
                val userId = call.parameters["userId"]!!.toUUID()
                call.respond(profileService.getProfile(userId))
            }
        }
    }
}
