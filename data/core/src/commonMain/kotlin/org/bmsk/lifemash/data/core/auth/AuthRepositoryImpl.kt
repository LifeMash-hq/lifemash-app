package org.bmsk.lifemash.data.core.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.bmsk.lifemash.data.local.auth.TokenStorage
import org.bmsk.lifemash.data.remote.auth.AuthApi
import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.domain.auth.AuthToken
import org.bmsk.lifemash.domain.auth.AuthUser

internal class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getCurrentUser(): Flow<AuthUser?> =
        tokenStorage.userJsonFlow().map { jsonStr ->
            jsonStr?.let { json.decodeFromString<AuthUser>(it) }
        }

    override suspend fun signInWithKakao(accessToken: String): AuthToken {
        val token = api.signInWithKakao(accessToken).toDomain()
        tokenStorage.saveTokens(token.accessToken, token.refreshToken)
        val user = api.getMe(token.accessToken).toDomain()
        tokenStorage.saveUserJson(json.encodeToString(AuthUser.serializer(), user))
        return token
    }

    override suspend fun signInWithGoogle(idToken: String): AuthToken {
        val token = api.signInWithGoogle(idToken).toDomain()
        tokenStorage.saveTokens(token.accessToken, token.refreshToken)
        val user = api.getMe(token.accessToken).toDomain()
        tokenStorage.saveUserJson(json.encodeToString(AuthUser.serializer(), user))
        return token
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthToken {
        val token = api.signInWithEmail(email, password).toDomain()
        tokenStorage.saveTokens(token.accessToken, token.refreshToken)
        val user = api.getMe(token.accessToken).toDomain()
        tokenStorage.saveUserJson(json.encodeToString(AuthUser.serializer(), user))
        return token
    }

    override suspend fun refreshToken(refreshToken: String): AuthToken {
        val token = api.refreshToken(refreshToken).toDomain()
        tokenStorage.saveTokens(token.accessToken, token.refreshToken)
        return token
    }

    override suspend fun signOut() {
        api.signOut()
        tokenStorage.clearTokens()
        tokenStorage.clearUser()
    }

    override suspend fun getStoredToken(): AuthToken? {
        val access = tokenStorage.getAccessToken() ?: return null
        val refresh = tokenStorage.getRefreshToken() ?: return null
        return AuthToken(accessToken = access, refreshToken = refresh)
    }

    override suspend fun saveToken(token: AuthToken) {
        tokenStorage.saveTokens(token.accessToken, token.refreshToken)
    }
}
