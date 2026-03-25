package org.bmsk.lifemash.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.plugins.currentUserId
import org.bmsk.lifemash.plugins.toUUID
import org.koin.ktor.ext.inject

/**
 * 그룹 관련 API 라우트.
 * 모든 엔드포인트가 JWT 인증 필요 (로그인한 사용자만 접근 가능).
 *
 * 엔드포인트 목록:
 *   POST   /api/v1/calendar/groups           → 새 그룹 생성
 *   POST   /api/v1/calendar/groups/join      → 초대 코드로 그룹 참여
 *   GET    /api/v1/calendar/groups           → 내가 속한 그룹 목록 조회
 *   GET    /api/v1/calendar/groups/{groupId} → 특정 그룹 상세 조회
 *   DELETE /api/v1/calendar/groups/{groupId} → 그룹 삭제 (OWNER만 가능)
 *   PATCH  /api/v1/calendar/groups/{groupId} → 그룹명 변경 (OWNER만 가능)
 */
fun Route.groupRoutes() {
    val groupService by inject<GroupService>()

    authenticate("auth-jwt") {
        post<Groups> {
            call.respond(groupService.create(call.currentUserId().toUUID(), call.receive()))
        }

        post<Groups.Join> {
            call.respond(groupService.join(call.currentUserId().toUUID(), call.receive()))
        }

        get<Groups> {
            call.respond(groupService.getMyGroups(call.currentUserId().toUUID()))
        }

        get<Groups.ById> { resource ->
            call.respond(groupService.getGroup(resource.groupId.toUUID()))
        }

        delete<Groups.ById> { resource ->
            groupService.delete(resource.groupId.toUUID(), call.currentUserId().toUUID())
            call.respond(HttpStatusCode.NoContent)
        }

        patch<Groups.ById> { resource ->
            call.respond(groupService.updateName(resource.groupId.toUUID(), call.currentUserId().toUUID(), call.receive()))
        }
    }
}
