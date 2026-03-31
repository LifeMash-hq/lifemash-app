package org.bmsk.lifemash.model.follow

import kotlinx.serialization.Serializable

@Serializable
data class UserSummaryDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)
