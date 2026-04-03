package org.bmsk.lifemash.auth.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.model.SocialProvider
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testToken = AuthToken("access", "refresh")
    private val existingUser = AuthUser(
        id = "u-1", email = "test@example.com", nickname = "테스터",
        profileImage = null, provider = SocialProvider.EMAIL, username = "tester",
    )
    private val newUser = AuthUser(
        id = "u-2", email = "new@example.com", nickname = "신규",
        profileImage = null, provider = SocialProvider.KAKAO, username = null,
    )

    private var signInResult: Result<AuthToken> = Result.success(testToken)
    private var currentUser: AuthUser? = existingUser

    private val fakeRepository = object : AuthRepository {
        override fun getCurrentUser(): Flow<AuthUser?> = flowOf(currentUser)
        override suspend fun signInWithKakao(accessToken: String): AuthToken = signInResult.getOrThrow()
        override suspend fun signInWithGoogle(idToken: String): AuthToken = signInResult.getOrThrow()
        override suspend fun signInWithEmail(email: String, password: String): AuthToken = signInResult.getOrThrow()
        override suspend fun refreshToken(refreshToken: String): AuthToken = testToken
        override suspend fun signOut() {}
        override suspend fun getStoredToken(): AuthToken? = null
        override suspend fun saveToken(token: AuthToken) {}
    }

    private fun createViewModel() = AuthViewModel(authRepository = fakeRepository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInResult = Result.success(testToken)
        currentUser = existingUser
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── 초기 상태 ─────────────────────────────────────────────────────────────

    @Test
    fun `초기 상태는 Idle이다`() {
        val viewModel = createViewModel()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    // ─── signInWithKakao ───────────────────────────────────────────────────────

    @Test
    fun `카카오 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithKakao("kakao-token")

        assertIs<AuthUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `카카오 로그인 성공 시 기존 사용자이면 isNewUser가 false이다`() = runTest(testDispatcher) {
        currentUser = existingUser // username = "tester"
        val viewModel = createViewModel()

        viewModel.signInWithKakao("kakao-token")

        val state = viewModel.uiState.value as AuthUiState.Success
        assertFalse(state.isNewUser)
    }

    @Test
    fun `카카오 로그인 성공 시 신규 사용자이면 isNewUser가 true이다`() = runTest(testDispatcher) {
        currentUser = newUser // username = null
        val viewModel = createViewModel()

        viewModel.signInWithKakao("kakao-token")

        val state = viewModel.uiState.value as AuthUiState.Success
        assertTrue(state.isNewUser)
    }

    @Test
    fun `카카오 로그인 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        signInResult = Result.failure(RuntimeException("네트워크 오류"))
        val viewModel = createViewModel()

        viewModel.signInWithKakao("bad-token")

        val state = viewModel.uiState.value
        assertIs<AuthUiState.Error>(state)
        assertEquals("네트워크 오류", state.message)
    }

    // ─── signInWithGoogle ──────────────────────────────────────────────────────

    @Test
    fun `구글 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithGoogle("google-id-token")

        assertIs<AuthUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `구글 로그인 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        signInResult = Result.failure(RuntimeException("구글 인증 오류"))
        val viewModel = createViewModel()

        viewModel.signInWithGoogle("bad-token")

        val state = viewModel.uiState.value
        assertIs<AuthUiState.Error>(state)
        assertEquals("구글 인증 오류", state.message)
    }

    // ─── signInWithEmail ───────────────────────────────────────────────────────

    @Test
    fun `이메일 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithEmail("user@example.com", "password123")

        assertIs<AuthUiState.Success>(viewModel.uiState.value)
    }

    @Test
    fun `이메일 로그인 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        signInResult = Result.failure(RuntimeException("비밀번호가 올바르지 않습니다"))
        val viewModel = createViewModel()

        viewModel.signInWithEmail("user@example.com", "wrong")

        val state = viewModel.uiState.value
        assertIs<AuthUiState.Error>(state)
        assertEquals("비밀번호가 올바르지 않습니다", state.message)
    }
}
