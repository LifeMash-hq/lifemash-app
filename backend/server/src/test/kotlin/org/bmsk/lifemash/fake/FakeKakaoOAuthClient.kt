package org.bmsk.lifemash.fake

import org.bmsk.lifemash.auth.oauth.KakaoOAuthClient
import org.bmsk.lifemash.auth.oauth.KakaoUser
import org.bmsk.lifemash.auth.oauth.KakaoAccount
import org.bmsk.lifemash.auth.oauth.KakaoProfile
import org.bmsk.lifemash.plugins.UnauthorizedException

class FakeKakaoOAuthClient : KakaoOAuthClient {
    var validToken: String = "valid-kakao-token"
    var user: KakaoUser = KakaoUser(
        id = 12345L,
        kakaoAccount = KakaoAccount(
            email = "test@kakao.com",
            profile = KakaoProfile(nickname = "카카오유저", profileImageUrl = "https://img.kakao.com/profile.jpg"),
        ),
    )

    override suspend fun getUser(accessToken: String): KakaoUser {
        if (accessToken != validToken) throw UnauthorizedException("Invalid Kakao token")
        return user
    }
}
