package org.bmsk.lifemash.data.remote.calendar.dto

import kotlinx.serialization.Serializable

@Serializable
data class FollowerDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
)
