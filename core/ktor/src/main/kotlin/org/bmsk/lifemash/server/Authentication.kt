package org.bmsk.lifemash.server

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlin.uuid.Uuid
import org.bmsk.lifemash.auth.JwtConfig
import org.bmsk.lifemash.plugins.UnauthorizedException

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun JWTPrincipal.userId(): String =
    payload.getClaim("userId").asString()

fun ApplicationCall.currentUserId(): String =
    principal<JWTPrincipal>()?.userId()
        ?: throw UnauthorizedException("인증 정보가 없습니다")

fun String.toUUID(): Uuid = Uuid.parse(this)
