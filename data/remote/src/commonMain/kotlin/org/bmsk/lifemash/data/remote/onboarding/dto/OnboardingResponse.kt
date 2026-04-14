package org.bmsk.lifemash.data.remote.onboarding.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckHandleResponse(val isAvailable: Boolean)

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val username: String? = null,
    val birthDate: String? = null,
)
