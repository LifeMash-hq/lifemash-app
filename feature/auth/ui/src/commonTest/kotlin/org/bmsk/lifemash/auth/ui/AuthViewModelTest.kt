package org.bmsk.lifemash.auth.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private var signInResult: Result<AuthToken> = Result.success(AuthToken("access", "refresh"))

    private val fakeRepository = object : AuthRepository {
        override fun getCurrentUser(): Flow<AuthUser?> = MutableStateFlow(null)
        override suspend fun signInWithKakao(accessToken: String): AuthToken = signInResult.getOrThrow()
        override suspend fun signInWithGoogle(idToken: String): AuthToken = signInResult.getOrThrow()
        override suspend fun signInWithEmail(email: String, password: String): AuthToken = signInResult.getOrThrow()
        override suspend fun refreshToken(refreshToken: String): AuthToken = AuthToken("new", "new")
        override suspend fun signOut() {}
        override suspend fun getStoredToken(): AuthToken? = null
        override suspend fun saveToken(token: AuthToken) {}
    }

    private fun createViewModel() = AuthViewModel(authRepository = fakeRepository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInResult = Result.success(AuthToken("access", "refresh"))
    }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `초기 상태는 Idle이다`() {
        val viewModel = createViewModel()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `카카오 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithKakao("token")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `구글 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithGoogle("token")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `이메일 로그인 성공 시 Success 상태가 된다`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.signInWithEmail("user@example.com", "password123")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `이메일 로그인 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        signInResult = Result.failure(RuntimeException("비밀번호가 올바르지 않습니다"))
        val viewModel = createViewModel()

        viewModel.signInWithEmail("user@example.com", "wrong")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals("비밀번호가 올바르지 않습니다", state.message)
    }

    @Test
    fun `로그인 실패 시 Error 상태가 된다`() = runTest(testDispatcher) {
        signInResult = Result.failure(RuntimeException("네트워크 오류"))
        val viewModel = createViewModel()

        viewModel.signInWithKakao("bad-token")

        val state = viewModel.uiState.value as AuthUiState.Error
        assertEquals("네트워크 오류", state.message)
    }
}
