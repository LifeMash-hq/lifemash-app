package org.bmsk.lifemash.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

/**
 * 전역 에러 핸들링 설정 (StatusPages 플러그인).
 *
 * 코드 어디서든 예외를 throw하면, 여기서 잡아서
 * 클라이언트에게 적절한 HTTP 상태코드 + JSON 에러 메시지를 반환한다.
 *
 * 예: throw NotFoundException("그룹을 찾을 수 없습니다")
 *     → HTTP 404 + { "message": "그룹을 찾을 수 없습니다" }
 *
 * 마지막 Throwable 핸들러는 예상치 못한 에러를 잡는 "안전망" 역할.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // 400 Bad Request — 클라이언트가 잘못된 요청을 보냈을 때
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        // 401 Unauthorized — 인증이 필요하거나 토큰이 유효하지 않을 때
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(cause.message ?: "Unauthorized"))
        }
        // 403 Forbidden — 인증은 됐지만 권한이 없을 때 (예: 그룹 멤버가 아닌 경우)
        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ErrorResponse(cause.message ?: "Forbidden"))
        }
        // 404 Not Found — 요청한 리소스가 존재하지 않을 때
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        // 409 Conflict — 리소스 충돌 (예: 중복 생성 시도)
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Conflict"))
        }
        // 400 Bad Request — 도메인 모델의 require() 실패 (잘못된 입력값)
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        // 500 Internal Server Error — 예상치 못한 서버 에러 (로그에 기록)
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }
}

/** 에러 응답 JSON 형식: { "message": "에러 내용" } */
@Serializable
data class ErrorResponse(val message: String)

// 예외 클래스들은 backend:api의 org.bmsk.lifemash.plugins.Exceptions.kt에 정의됨
