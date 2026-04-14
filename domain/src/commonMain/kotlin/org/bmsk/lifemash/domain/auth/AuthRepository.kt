package org.bmsk.lifemash.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<AuthUser?>
    suspend fun signInWithKakao(accessToken: String): AuthToken
    suspend fun signInWithGoogle(idToken: String): AuthToken
    suspend fun signInWithEmail(email: String, password: String): AuthToken
    suspend fun refreshToken(refreshToken: String): AuthToken
    suspend fun signOut()
    suspend fun getStoredToken(): AuthToken?
    suspend fun saveToken(token: AuthToken)
}
