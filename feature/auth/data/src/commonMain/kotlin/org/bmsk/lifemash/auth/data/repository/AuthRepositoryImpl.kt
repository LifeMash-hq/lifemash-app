package org.bmsk.lifemash.auth.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bmsk.lifemash.auth.data.api.AuthApi
import org.bmsk.lifemash.auth.data.storage.TokenStorage
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)

    override fun getCurrentUser(): Flow<AuthUser?> = _currentUser.asStateFlow()

    override suspend fun signInWithKakao(accessToken: String): AuthToken {
        val token = api.signInWithKakao(accessToken).toDomain()
        tokenStorage.save(token)
        refreshCurrentUser()
        return token
    }

    override suspend fun signInWithGoogle(idToken: String): AuthToken {
        val token = api.signInWithGoogle(idToken).toDomain()
        tokenStorage.save(token)
        refreshCurrentUser()
        return token
    }

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val token = api.refreshToken(refreshToken).toDomain()
        tokenStorage.save(token)
        return token
    }

    override suspend fun signOut() {
        api.signOut()
        tokenStorage.clear()
        _currentUser.value = null
    }

    override suspend fun getStoredToken(): AuthToken? = tokenStorage.get()

    override suspend fun saveToken(token: AuthToken) = tokenStorage.save(token)

    private suspend fun refreshCurrentUser() {
        _currentUser.value = api.getMe().toDomain()
    }
}
