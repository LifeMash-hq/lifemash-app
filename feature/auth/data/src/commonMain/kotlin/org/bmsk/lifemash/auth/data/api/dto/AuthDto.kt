package org.bmsk.lifemash.auth.data.api.dto

import kotlinx.serialization.Serializable
import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.model.SocialProvider

@Serializable
data class AuthTokenDto(
    val accessToken: String,
    val refreshToken: String,
) {
    fun toDomain() = AuthToken(accessToken = accessToken, refreshToken = refreshToken)
}

@Serializable
data class AuthUserDto(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val provider: SocialProvider,
) {
    fun toDomain() = AuthUser(
        id = id,
        email = email,
        nickname = nickname,
        profileImage = profileImage,
        provider = provider,
    )
}

@Serializable
data class KakaoSignInBody(val accessToken: String)

@Serializable
data class GoogleSignInBody(val idToken: String)

@Serializable
data class EmailSignInBody(val email: String, val password: String)

@Serializable
data class RefreshTokenBody(val refreshToken: String)
