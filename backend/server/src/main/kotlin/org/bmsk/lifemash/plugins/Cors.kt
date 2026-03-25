package org.bmsk.lifemash.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

/**
 * CORS(Cross-Origin Resource Sharing) 설정.
 *
 * 브라우저는 보안상 다른 도메인의 API 호출을 기본적으로 차단한다.
 * 예: 프론트엔드가 localhost:3000이고 백엔드가 api.lifemash.com이면
 *     브라우저가 "출처가 다르다"며 요청을 거부함.
 *
 * CORS 설정으로 이를 허용:
 * - anyHost(): 모든 도메인에서의 요청 허용 (개발/프로토타입 용도, 운영 시 제한 필요)
 * - allowHeader: 요청에 포함할 수 있는 HTTP 헤더 (Content-Type, Authorization 등)
 * - allowMethod: 허용할 HTTP 메서드 (GET, POST, PUT, PATCH, DELETE)
 */
fun Application.configureCors() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
}
