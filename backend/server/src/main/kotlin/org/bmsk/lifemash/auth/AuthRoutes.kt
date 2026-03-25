package org.bmsk.lifemash.auth

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.auth.GoogleSignInRequest
import org.bmsk.lifemash.model.auth.KakaoSignInRequest
import org.bmsk.lifemash.model.auth.RefreshTokenRequest
import org.bmsk.lifemash.plugins.userId
import org.koin.ktor.ext.inject

/**
 * 인증(Auth) 관련 API 라우트 정의.
 *
 * 라우트(Route)란? "이 URL로 이런 HTTP 메서드 요청이 오면 이 코드를 실행해라"는 매핑.
 *
 * 엔드포인트 목록:
 *   POST /api/v1/auth/kakao   → 카카오 로그인 (토큰 발급)
 *   POST /api/v1/auth/google  → 구글 로그인 (토큰 발급)
 *   POST /api/v1/auth/refresh → 토큰 갱신
 *   POST /api/v1/auth/signout → 로그아웃 (JWT 인증 필요)
 *   GET  /api/v1/auth/me      → 내 프로필 조회 (JWT 인증 필요)
 */
fun Route.authRoutes() {
    // Koin DI에서 AuthService 인스턴스를 가져옴
    val authService by inject<AuthService>()

    route("/auth") {
        // 카카오 로그인: 앱이 카카오 SDK로 받은 accessToken을 서버에 전달
        post("/kakao") {
            val request = call.receive<KakaoSignInRequest>()  // JSON 본문 → 객체 변환
            val token = authService.signInWithKakao(request.accessToken)
            call.respond(token)  // 생성된 JWT를 JSON으로 응답
        }

        // 구글 로그인: 앱이 구글 SDK로 받은 idToken을 서버에 전달
        post("/google") {
            val request = call.receive<GoogleSignInRequest>()
            val token = authService.signInWithGoogle(request.idToken)
            call.respond(token)
        }

        // 토큰 갱신: accessToken 만료 시 refreshToken으로 새 토큰 쌍 발급
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val token = authService.refreshToken(request.refreshToken)
            call.respond(token)
        }

        // authenticate 블록 안의 API는 유효한 JWT가 있어야만 접근 가능
        authenticate("auth-jwt") {
            // 로그아웃: 서버 측은 stateless(상태 없음)이므로 204만 반환.
            // 실제 로그아웃은 클라이언트가 저장된 토큰을 삭제하는 방식.
            post("/signout") {
                call.respond(HttpStatusCode.NoContent)
            }

            // 내 프로필 조회: JWT에서 userId를 추출하여 사용자 정보 반환
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val user = authService.getMe(principal.userId())
                call.respond(user)
            }
        }
    }
}
