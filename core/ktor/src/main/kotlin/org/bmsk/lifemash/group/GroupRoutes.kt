package org.bmsk.lifemash.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.group.GroupService
import org.bmsk.lifemash.server.currentUserId
import org.bmsk.lifemash.server.toUUID
import org.koin.ktor.ext.inject

fun Route.groupRoutes() {
    val groupService by inject<GroupService>()

    authenticate("auth-jwt") {
        route("/calendar/groups") {
            post {
                call.respond(groupService.create(call.currentUserId().toUUID(), call.receive()))
            }

            post("/join") {
                call.respond(groupService.join(call.currentUserId().toUUID(), call.receive()))
            }

            get {
                call.respond(groupService.getMyGroups(call.currentUserId().toUUID()))
            }

            get("/{groupId}") {
                val groupId = call.parameters["groupId"]!!
                call.respond(groupService.getGroup(groupId.toUUID()))
            }

            delete("/{groupId}") {
                val groupId = call.parameters["groupId"]!!
                groupService.delete(groupId.toUUID(), call.currentUserId().toUUID())
                call.respond(HttpStatusCode.NoContent)
            }

            patch("/{groupId}") {
                val groupId = call.parameters["groupId"]!!
                call.respond(groupService.updateName(groupId.toUUID(), call.currentUserId().toUUID(), call.receive()))
            }
        }
    }
}
