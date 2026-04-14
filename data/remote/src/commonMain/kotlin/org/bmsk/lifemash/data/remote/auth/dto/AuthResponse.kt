package org.bmsk.lifemash.data.remote.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class AuthUserResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val provider: String,
    val username: String? = null,
)

@Serializable
data class KakaoSignInRequest(val accessToken: String)

@Serializable
data class GoogleSignInRequest(val idToken: String)

@Serializable
data class EmailSignInRequest(val email: String, val password: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)
