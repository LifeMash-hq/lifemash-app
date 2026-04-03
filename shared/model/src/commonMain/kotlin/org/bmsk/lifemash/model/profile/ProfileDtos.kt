package org.bmsk.lifemash.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val email: String,
    val nickname: String,
    val bio: String? = null,
    val profileImage: String? = null,
    val provider: String,
    val followerCount: Int,
    val followingCount: Int,
    val isFollowing: Boolean? = null,
)

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val bio: String? = null,
    val profileImage: String? = null,
)
