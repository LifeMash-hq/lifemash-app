package org.bmsk.lifemash.auth

import kotlinx.coroutines.test.runTest
import org.bmsk.lifemash.fake.FakeGoogleOAuthClient
import org.bmsk.lifemash.fake.FakeKakaoOAuthClient
import org.bmsk.lifemash.fake.FakeUserRepository
import org.bmsk.lifemash.plugins.UnauthorizedException
import java.util.*
import kotlin.test.*

class AuthServiceTest {

    // ── JWT 관련 테스트 (기존 유지) ──

    @Test
    fun `JWT 액세스 토큰이 생성되고 검증된다`() {
        // Given
        val userId = "test-user-id"

        // When
        val token = JwtConfig.generateAccessToken(userId)

        // Then
        assertNotNull(token)
        val decoded = JwtConfig.verifier.verify(token)
        assertEquals(userId, decoded.getClaim("userId").asString())
    }

    @Test
    fun `JWT 리프레시 토큰이 생성되고 검증된다`() {
        // Given
        val userId = "test-user-id"

        // When
        val refreshToken = JwtConfig.generateRefreshToken(userId)
        val verifiedUserId = JwtConfig.verifyRefreshToken(refreshToken)

        // Then
        assertEquals(userId, verifiedUserId)
    }

    @Test
    fun `잘못된 리프레시 토큰은 null을 반환한다`() {
        // When
        val result = JwtConfig.verifyRefreshToken("invalid-token")

        // Then
        assertNull(result)
    }

    @Test
    fun `액세스 토큰을 리프레시 토큰으로 사용하면 null을 반환한다`() {
        // Given
        val accessToken = JwtConfig.generateAccessToken("user-id")

        // When
        val result = JwtConfig.verifyRefreshToken(accessToken)

        // Then
        assertNull(result)
    }

    // ── AuthService 비즈니스 로직 테스트 ──

    private fun createAuthService(
        userRepo: FakeUserRepository = FakeUserRepository(),
        kakao: FakeKakaoOAuthClient = FakeKakaoOAuthClient(),
        google: FakeGoogleOAuthClient = FakeGoogleOAuthClient(),
    ) = Triple(AuthServiceImpl(userRepo, kakao, google), userRepo, kakao)

    @Test
    fun `카카오 로그인 시 신규 사용자가 생성되고 토큰이 발급된다`() = runTest {
        // Given
        val (service, userRepo) = createAuthService()

        // When
        val result = service.signInWithKakao("valid-kakao-token")

        // Then
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        val userId = JwtConfig.verifier.verify(result.accessToken).getClaim("userId").asString()
        val user = userRepo.findById(UUID.fromString(userId))
        assertNotNull(user)
        assertEquals("KAKAO", user.provider)
    }

    @Test
    fun `카카오 로그인 시 기존 사용자 정보가 업데이트된다`() = runTest {
        // Given
        val kakao = FakeKakaoOAuthClient()
        val (service) = createAuthService(kakao = kakao)

        // When — 첫 로그인
        val first = service.signInWithKakao("valid-kakao-token")
        val userId1 = JwtConfig.verifier.verify(first.accessToken).getClaim("userId").asString()

        // When — 닉네임 변경 후 재로그인
        kakao.user = kakao.user.copy(
            kakaoAccount = org.bmsk.lifemash.auth.oauth.KakaoAccount(
                email = "test@kakao.com",
                profile = org.bmsk.lifemash.auth.oauth.KakaoProfile(nickname = "변경된닉네임"),
            ),
        )
        val second = service.signInWithKakao("valid-kakao-token")
        val userId2 = JwtConfig.verifier.verify(second.accessToken).getClaim("userId").asString()

        // Then — 같은 사용자
        assertEquals(userId1, userId2)
    }

    @Test
    fun `구글 로그인 시 신규 사용자가 생성되고 토큰이 발급된다`() = runTest {
        // Given
        val (service, userRepo) = createAuthService()

        // When
        val result = service.signInWithGoogle("valid-google-token")

        // Then
        assertNotNull(result.accessToken)
        val userId = JwtConfig.verifier.verify(result.accessToken).getClaim("userId").asString()
        val user = userRepo.findById(UUID.fromString(userId))
        assertNotNull(user)
        assertEquals("GOOGLE", user.provider)
    }

    @Test
    fun `유효하지 않은 카카오 토큰은 UnauthorizedException을 발생시킨다`() = runTest {
        // Given
        val (service) = createAuthService()

        // When & Then
        assertFailsWith<UnauthorizedException> {
            service.signInWithKakao("invalid-token")
        }
    }

    @Test
    fun `유효하지 않은 구글 토큰은 UnauthorizedException을 발생시킨다`() = runTest {
        // Given
        val (service) = createAuthService()

        // When & Then
        assertFailsWith<UnauthorizedException> {
            service.signInWithGoogle("invalid-token")
        }
    }

    @Test
    fun `refreshToken으로 새 토큰 쌍이 발급된다`() {
        // Given
        val userId = "test-user-id"
        val (service) = createAuthService()
        val refreshToken = JwtConfig.generateRefreshToken(userId)

        // When
        val result = service.refreshToken(refreshToken)

        // Then
        assertNotNull(result.accessToken)
        assertNotNull(result.refreshToken)
        val verified = JwtConfig.verifier.verify(result.accessToken).getClaim("userId").asString()
        assertEquals(userId, verified)
    }

    @Test
    fun `유효하지 않은 refreshToken은 UnauthorizedException을 발생시킨다`() {
        // Given
        val (service) = createAuthService()

        // When & Then
        assertFailsWith<UnauthorizedException> {
            service.refreshToken("bad-token")
        }
    }

    @Test
    fun `getMe로 사용자 정보를 조회한다`() = runTest {
        // Given
        val (service) = createAuthService()
        val tokens = service.signInWithKakao("valid-kakao-token")
        val userId = JwtConfig.verifier.verify(tokens.accessToken).getClaim("userId").asString()

        // When
        val me = service.getMe(userId)

        // Then
        assertEquals(userId, me.id)
        assertEquals("KAKAO", me.provider)
    }

    @Test
    fun `존재하지 않는 사용자 ID는 UnauthorizedException을 발생시킨다`() {
        // Given
        val (service) = createAuthService()

        // When & Then
        assertFailsWith<UnauthorizedException> {
            service.getMe(UUID.randomUUID().toString())
        }
    }
}
