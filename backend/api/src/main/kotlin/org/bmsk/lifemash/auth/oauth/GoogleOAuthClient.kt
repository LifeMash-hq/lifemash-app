package org.bmsk.lifemash.auth.oauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface GoogleOAuthClient {
    suspend fun verifyIdToken(idToken: String): GoogleUser
}

@Serializable
data class GoogleUser(
    val sub: String,
    val email: String,
    val name: String? = null,
    @SerialName("picture") val picture: String? = null,
) {
    val nickname: String get() = name ?: "Google 사용자"
    val profileImage: String? get() = picture
}
