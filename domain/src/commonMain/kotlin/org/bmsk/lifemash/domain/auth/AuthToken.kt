package org.bmsk.lifemash.domain.auth

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)
