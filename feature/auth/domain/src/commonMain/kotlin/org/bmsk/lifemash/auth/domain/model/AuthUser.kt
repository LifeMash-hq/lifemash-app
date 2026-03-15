package org.bmsk.lifemash.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val provider: SocialProvider,
)

@Serializable
enum class SocialProvider { KAKAO, GOOGLE }
