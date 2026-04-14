package org.bmsk.lifemash.data.core.auth

import org.bmsk.lifemash.data.remote.auth.dto.AuthTokenResponse
import org.bmsk.lifemash.data.remote.auth.dto.AuthUserResponse
import org.bmsk.lifemash.domain.auth.AuthToken
import org.bmsk.lifemash.domain.auth.AuthUser
import org.bmsk.lifemash.domain.auth.SocialProvider

internal fun AuthTokenResponse.toDomain(): AuthToken =
    AuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

internal fun AuthUserResponse.toDomain(): AuthUser =
    AuthUser(
        id = id,
        email = email,
        nickname = nickname,
        profileImage = profileImage,
        provider = SocialProvider.valueOf(provider),
        username = username,
    )
