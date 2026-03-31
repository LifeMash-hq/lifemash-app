package org.bmsk.lifemash.auth

import org.bmsk.lifemash.auth.oauth.GoogleOAuthClient
import org.bmsk.lifemash.auth.oauth.KakaoOAuthClient
import org.bmsk.lifemash.model.auth.AuthTokenDto
import org.bmsk.lifemash.model.auth.AuthUserDto
import org.bmsk.lifemash.plugins.UnauthorizedException
import org.bmsk.lifemash.user.UserRepository
import kotlin.uuid.Uuid

/**
 * 인증 비즈니스 로직을 담당하는 서비스.
 *
 * 소셜 로그인 흐름:
 * 1. 앱에서 카카오/구글 SDK로 로그인 → 소셜 토큰(accessToken/idToken)을 받음
 * 2. 앱이 해당 토큰을 이 서버로 전송
 * 3. 서버가 카카오/구글 API에 토큰을 보내 사용자 정보를 확인
 * 4. DB에 사용자를 저장(또는 업데이트)하고 JWT 토큰 쌍을 발급
 */
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val kakaoClient: KakaoOAuthClient,
    private val googleClient: GoogleOAuthClient,
) : AuthService {
    /** 카카오 로그인: 카카오 accessToken으로 사용자 정보 확인 후 JWT 발급 */
    override suspend fun signInWithKakao(accessToken: String): AuthTokenDto {
        val kakaoUser = kakaoClient.getUser(accessToken)
        // upsert: 기존 사용자면 정보 업데이트, 신규 사용자면 생성
        val user = userRepository.upsert(
            email = kakaoUser.email,
            provider = "KAKAO",
            providerId = kakaoUser.id.toString(),
            nickname = kakaoUser.nickname,
            profileImage = kakaoUser.profileImage,
        )
        return generateTokens(user)
    }

    /** 구글 로그인: 구글 idToken(ID 토큰)을 검증하여 사용자 정보 확인 후 JWT 발급 */
    override suspend fun signInWithGoogle(idToken: String): AuthTokenDto {
        val googleUser = googleClient.verifyIdToken(idToken)
        val user = userRepository.upsert(
            email = googleUser.email,
            provider = "GOOGLE",
            providerId = googleUser.sub,
            nickname = googleUser.nickname,
            profileImage = googleUser.profileImage,
        )
        return generateTokens(user)
    }

    /** accessToken이 만료되었을 때 refreshToken으로 새 토큰 쌍을 재발급 */
    override fun refreshToken(refreshToken: String): AuthTokenDto {
        val userId = JwtConfig.verifyRefreshToken(refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")
        return AuthTokenDto(
            accessToken = JwtConfig.generateAccessToken(userId),
            refreshToken = JwtConfig.generateRefreshToken(userId),
        )
    }

    /** 현재 로그인한 사용자의 프로필 정보 조회 */
    override fun getMe(userId: String): AuthUserDto {
        return userRepository.findById(Uuid.parse(userId))
            ?: throw UnauthorizedException("User not found")
    }

    /** 사용자 정보로 accessToken + refreshToken 쌍을 생성하는 내부 헬퍼 */
    private fun generateTokens(user: AuthUserDto) = AuthTokenDto(
        accessToken = JwtConfig.generateAccessToken(user.id),
        refreshToken = JwtConfig.generateRefreshToken(user.id),
    )
}
