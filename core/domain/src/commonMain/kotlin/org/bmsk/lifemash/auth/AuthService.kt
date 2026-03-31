package org.bmsk.lifemash.auth

import org.bmsk.lifemash.model.auth.AuthTokenDto
import org.bmsk.lifemash.model.auth.AuthUserDto

interface AuthService {
    suspend fun signInWithKakao(accessToken: String): AuthTokenDto
    suspend fun signInWithGoogle(idToken: String): AuthTokenDto
    fun refreshToken(refreshToken: String): AuthTokenDto
    fun getMe(userId: String): AuthUserDto
}
