package org.bmsk.lifemash.auth.oauth

class StubKakaoOAuthClient : KakaoOAuthClient {
    override suspend fun getUser(accessToken: String): KakaoUser =
        KakaoUser(id = 12345, kakaoAccount = null)
}
