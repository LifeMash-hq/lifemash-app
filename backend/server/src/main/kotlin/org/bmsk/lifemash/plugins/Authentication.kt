package org.bmsk.lifemash.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.bmsk.lifemash.auth.JwtConfig
import java.util.*

/**
 * JWT(JSON Web Token) 인증 설정.
 *
 * JWT란? 로그인 성공 시 서버가 발급하는 "디지털 신분증".
 * 클라이언트는 이후 모든 API 요청에 이 토큰을 헤더에 포함한다.
 * 서버는 토큰의 서명을 검증하여 위조 여부를 확인한다.
 *
 * "auth-jwt"라는 이름으로 등록해두고,
 * 라우트에서 authenticate("auth-jwt") { ... }로 감싸면
 * 해당 API는 유효한 JWT가 있어야만 접근 가능하다.
 *
 * validate 블록:
 *   토큰 안에 "userId" 클레임(데이터)이 있으면 → 인증 성공 (JWTPrincipal 반환)
 *   없으면 → null 반환 → 401 Unauthorized 응답
 */
fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("userId")?.asString()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

/** 인증된 요청에서 현재 로그인한 사용자의 ID를 꺼내는 편의 확장함수 */
fun JWTPrincipal.userId(): String =
    payload.getClaim("userId").asString()

/** authenticate 블록 안에서 현재 인증된 사용자 ID를 반환. 미인증 시 401. */
fun ApplicationCall.currentUserId(): String =
    principal<JWTPrincipal>()?.userId()
        ?: throw UnauthorizedException("인증 정보가 없습니다")
