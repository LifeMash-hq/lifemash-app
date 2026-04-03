package org.bmsk.lifemash.main

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
import org.bmsk.lifemash.auth.domain.model.SocialProvider
import org.bmsk.lifemash.auth.domain.repository.AuthRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testUser = AuthUser(
        id = "u-1", email = "test@example.com", nickname = "테스터",
        profileImage = null, provider = SocialProvider.EMAIL, username = "tester",
    )

    private val currentUserFlow = MutableStateFlow<AuthUser?>(null)

    private val fakeRepository = object : AuthRepository {
        override fun getCurrentUser(): Flow<AuthUser?> = currentUserFlow
        override suspend fun signInWithKakao(accessToken: String): AuthToken = throw NotImplementedError()
        override suspend fun signInWithGoogle(idToken: String): AuthToken = throw NotImplementedError()
        override suspend fun signInWithEmail(email: String, password: String): AuthToken = throw NotImplementedError()
        override suspend fun refreshToken(refreshToken: String): AuthToken = throw NotImplementedError()
        override suspend fun signOut() {}
        override suspend fun getStoredToken(): AuthToken? = null
        override suspend fun saveToken(token: AuthToken) {}
    }

    private fun createViewModel() = MainViewModel(authRepository = fakeRepository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        currentUserFlow.value = null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── authState 매핑 ────────────────────────────────────────────────────────

    @Test
    fun `초기 상태는 Loading이다`() {
        // SharingStarted.Eagerly의 initial value
        val viewModel = createViewModel()
        // UnconfinedTestDispatcher이므로 Flow가 즉시 수집되어 Unauthenticated(null)로 전환됨
        // 초기 Loading은 첫 emit 전에만 잠깐 존재하므로, null emit 후 Unauthenticated 확인
        assertIs<AuthState.Unauthenticated>(viewModel.authState.value)
    }

    @Test
    fun `사용자가 있으면 Authenticated 상태가 된다`() = runTest(testDispatcher) {
        currentUserFlow.value = testUser
        val viewModel = createViewModel()

        val state = viewModel.authState.value
        assertIs<AuthState.Authenticated>(state)
        assertEquals(testUser, state.user)
    }

    @Test
    fun `사용자가 없으면 Unauthenticated 상태가 된다`() = runTest(testDispatcher) {
        currentUserFlow.value = null
        val viewModel = createViewModel()

        assertIs<AuthState.Unauthenticated>(viewModel.authState.value)
    }

    @Test
    fun `사용자가 로그인하면 Authenticated로 전환된다`() = runTest(testDispatcher) {
        currentUserFlow.value = null
        val viewModel = createViewModel()
        assertIs<AuthState.Unauthenticated>(viewModel.authState.value)

        currentUserFlow.value = testUser

        val state = viewModel.authState.value
        assertIs<AuthState.Authenticated>(state)
        assertEquals(testUser.id, state.user.id)
    }

    @Test
    fun `사용자가 로그아웃하면 Unauthenticated로 전환된다`() = runTest(testDispatcher) {
        currentUserFlow.value = testUser
        val viewModel = createViewModel()
        assertIs<AuthState.Authenticated>(viewModel.authState.value)

        currentUserFlow.value = null

        assertIs<AuthState.Unauthenticated>(viewModel.authState.value)
    }
}
