package org.bmsk.lifemash.fake

import org.bmsk.lifemash.auth.oauth.GoogleOAuthClient
import org.bmsk.lifemash.auth.oauth.GoogleUser
import org.bmsk.lifemash.plugins.UnauthorizedException

class FakeGoogleOAuthClient : GoogleOAuthClient {
    var validToken: String = "valid-google-token"
    var user: GoogleUser = GoogleUser(
        sub = "google-sub-123",
        email = "test@gmail.com",
        name = "구글유저",
        picture = "https://lh3.googleusercontent.com/photo.jpg",
    )

    override suspend fun verifyIdToken(idToken: String): GoogleUser {
        if (idToken != validToken) throw UnauthorizedException("Invalid Google token")
        return user
    }
}
