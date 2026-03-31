package org.bmsk.lifemash.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * JWT(JSON Web Token) 토큰 생성/검증 설정.
 *
 * JWT 인증 흐름:
 * 1. 사용자가 카카오/구글 로그인 성공
 * 2. 서버가 accessToken(30분) + refreshToken(30일)을 발급
 * 3. 클라이언트는 API 호출 시 accessToken을 Authorization 헤더에 포함
 * 4. accessToken 만료 시 refreshToken으로 새 토큰 쌍을 재발급
 *
 * HMAC256: 비밀키(secret)로 토큰 서명 → 토큰 위조 방지
 * claim: 토큰 안에 담기는 데이터 (여기선 userId)
 */
object JwtConfig {
    // 환경변수에서 비밀키를 가져옴. 개발 환경에서는 기본값 사용
    private val secret: String get() = org.bmsk.lifemash.config.EnvConfig.get("JWT_SECRET") ?: "dev-secret"
    private val refreshSecret: String get() = org.bmsk.lifemash.config.EnvConfig.get("JWT_REFRESH_SECRET") ?: "dev-refresh-secret"
    private const val ISSUER = "lifemash-backend"  // 토큰 발급자 식별 문자열
    private const val ACCESS_EXPIRY_MS = 30L * 60 * 1000          // 30분
    private const val REFRESH_EXPIRY_MS = 30L * 24 * 60 * 60 * 1000 // 30일

    /** accessToken 검증기 — 들어온 토큰이 유효한지 서명과 발급자를 확인 */
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(secret))
        .withIssuer(ISSUER)
        .build()

    /** 로그인 성공 시 발급하는 단기 토큰 (30분 유효). API 호출에 사용. */
    fun generateAccessToken(userId: String): String = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_EXPIRY_MS))
        .sign(Algorithm.HMAC256(secret))

    /** accessToken 만료 시 새 토큰을 발급받기 위한 장기 토큰 (30일 유효). */
    fun generateRefreshToken(userId: String): String = JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_EXPIRY_MS))
        .sign(Algorithm.HMAC256(refreshSecret))

    /** refreshToken을 검증하고, 유효하면 토큰에 담긴 userId를 반환. 유효하지 않으면 null. */
    fun verifyRefreshToken(token: String): String? = runCatching {
        JWT.require(Algorithm.HMAC256(refreshSecret))
            .withIssuer(ISSUER)
            .build()
            .verify(token)
            .getClaim("userId")
            .asString()
    }.getOrNull()
}
