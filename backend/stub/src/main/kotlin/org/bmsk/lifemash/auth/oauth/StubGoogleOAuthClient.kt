package org.bmsk.lifemash.auth.oauth

class StubGoogleOAuthClient : GoogleOAuthClient {
    override suspend fun verifyIdToken(idToken: String): GoogleUser =
        GoogleUser(sub = "demo", email = "demo@google.com")
}
