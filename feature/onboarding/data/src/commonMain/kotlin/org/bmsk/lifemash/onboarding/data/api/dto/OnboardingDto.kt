package org.bmsk.lifemash.onboarding.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckHandleResponse(val isAvailable: Boolean)

@Serializable
data class UpdateProfileBody(
    val nickname: String? = null,
    val username: String? = null,
    val birthDate: String? = null,
)
