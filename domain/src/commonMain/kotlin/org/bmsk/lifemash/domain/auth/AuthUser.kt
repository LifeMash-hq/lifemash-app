package org.bmsk.lifemash.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val provider: SocialProvider,
    val username: String? = null,
)

@Serializable
enum class SocialProvider { KAKAO, GOOGLE, EMAIL }
