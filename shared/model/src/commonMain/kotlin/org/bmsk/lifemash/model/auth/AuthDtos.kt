package org.bmsk.lifemash.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenDto(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class AuthUserDto(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String? = null,
    val provider: String,
)

@Serializable
data class KakaoSignInRequest(val accessToken: String)

@Serializable
data class GoogleSignInRequest(val idToken: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class EmailSignInRequest(val email: String, val password: String)
