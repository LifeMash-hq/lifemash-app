package org.bmsk.lifemash.auth.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.model.SocialProvider

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
