package org.bmsk.lifemash.calendar.data.api.dto

import kotlinx.serialization.Serializable
import org.bmsk.lifemash.calendar.domain.model.Follower

@Serializable
data class FollowerDto(
    val id: String,
    val nickname: String,
    val profileImage: String? = null,
) {
    fun toDomain() = Follower(id = id, nickname = nickname, profileImage = profileImage)
}
