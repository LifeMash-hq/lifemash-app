package org.bmsk.lifemash.data.remote.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    val nickname: String,
    val bio: String? = null,
    val profileImage: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val bio: String? = null,
    val profileImage: String? = null
)

@Serializable
data class MomentMediaDto(
    val mediaUrl: String,
    val mediaType: String,
    val sortOrder: Int,
)

@Serializable
data class MomentDto(
    val id: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val authorId: String,
    val authorNickname: String,
    val media: List<MomentMediaDto> = emptyList(),
    val caption: String? = null,
    val visibility: String = "public",
    val createdAt: String,
)
