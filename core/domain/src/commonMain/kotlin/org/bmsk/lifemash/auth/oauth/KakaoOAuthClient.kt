package org.bmsk.lifemash.auth.oauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface KakaoOAuthClient {
    suspend fun getUser(accessToken: String): KakaoUser
}

@Serializable
data class KakaoUser(
    val id: Long,
    @SerialName("kakao_account") val kakaoAccount: KakaoAccount? = null,
) {
    val email: String get() = kakaoAccount?.email ?: "${id}@kakao.lifemash"
    val nickname: String get() = kakaoAccount?.profile?.nickname ?: "카카오 사용자"
    val profileImage: String? get() = kakaoAccount?.profile?.profileImageUrl
}

@Serializable
data class KakaoAccount(
    val email: String? = null,
    val profile: KakaoProfile? = null,
)

@Serializable
data class KakaoProfile(
    val nickname: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
)
