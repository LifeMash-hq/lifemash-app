package org.bmsk.lifemash.auth

import org.bmsk.lifemash.model.auth.AuthTokenDto
import org.bmsk.lifemash.model.auth.AuthUserDto

class StubAuthService : AuthService {
    override suspend fun signInWithKakao(accessToken: String): AuthTokenDto =
        AuthTokenDto("demo-access-token", "demo-refresh-token")

    override suspend fun signInWithGoogle(idToken: String): AuthTokenDto =
        AuthTokenDto("demo-access-token", "demo-refresh-token")

    override fun refreshToken(refreshToken: String): AuthTokenDto =
        AuthTokenDto("demo-access-token", "demo-refresh-token")

    override fun getMe(userId: String): AuthUserDto =
        AuthUserDto(
            id = userId,
            email = "demo@lifemash.app",
            nickname = "Demo User",
            provider = "KAKAO",
        )
}
