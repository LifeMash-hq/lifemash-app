package org.bmsk.lifemash.auth.data.api.dto

import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.model.SocialProvider
import org.bmsk.lifemash.model.auth.AuthTokenDto
import org.bmsk.lifemash.model.auth.AuthUserDto

fun AuthTokenDto.toDomain() = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
)

fun AuthUserDto.toDomain() = AuthUser(
    id = id,
    email = email,
    nickname = nickname,
    profileImage = profileImage,
    provider = SocialProvider.valueOf(provider),
)
