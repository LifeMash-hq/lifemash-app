package org.bmsk.lifemash.auth

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.auth.AuthService
import org.bmsk.lifemash.model.auth.GoogleSignInRequest
import org.bmsk.lifemash.model.auth.KakaoSignInRequest
import org.bmsk.lifemash.model.auth.RefreshTokenRequest
import org.bmsk.lifemash.server.userId
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<AuthService>()

    route("/auth") {
        post("/kakao") {
            val request = call.receive<KakaoSignInRequest>()
            val token = authService.signInWithKakao(request.accessToken)
            call.respond(token)
        }

        post("/google") {
            val request = call.receive<GoogleSignInRequest>()
            val token = authService.signInWithGoogle(request.idToken)
            call.respond(token)
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val token = authService.refreshToken(request.refreshToken)
            call.respond(token)
        }

        authenticate("auth-jwt") {
            post("/signout") {
                call.respond(HttpStatusCode.NoContent)
            }

            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val user = authService.getMe(principal.userId())
                call.respond(user)
            }
        }
    }
}
