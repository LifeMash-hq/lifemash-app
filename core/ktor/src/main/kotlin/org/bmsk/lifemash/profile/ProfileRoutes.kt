package org.bmsk.lifemash.profile

import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.profile.UpdateProfileRequest
import org.bmsk.lifemash.profile.ProfileService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject
import org.bmsk.lifemash.plugins.BadRequestException

fun Route.profileRoutes() {
    val profileService by inject<ProfileService>()

    authenticate("auth-jwt") {
        route("/profile") {
            get("/check-handle") {
                val handle = call.request.queryParameters["id"] 
                    ?: throw BadRequestException("id 파라미터가 필요합니다.")
                val isAvailable = profileService.checkHandleAvailability(handle)
                call.respond(mapOf("isAvailable" to isAvailable))
            }

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
                val viewerId = call.currentUserId().toUUID()
                call.respond(profileService.getProfile(userId, viewerId))
            }
        }
    }
}
