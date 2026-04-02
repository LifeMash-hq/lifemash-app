package org.bmsk.lifemash.profile.data.api.dto

import kotlinx.serialization.Serializable
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.ProfileMomentMedia
import org.bmsk.lifemash.profile.domain.model.UserProfile

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    val nickname: String,
    val bio: String? = null,
    val profileImage: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
) {
    fun toDomain(isFollowing: Boolean = false) = UserProfile(
        id = id, email = email, nickname = nickname, bio = bio,
        profileImage = profileImage, followerCount = followerCount,
        followingCount = followingCount, isFollowing = isFollowing,
    )
}

@Serializable
data class UpdateProfileBody(val nickname: String? = null, val bio: String? = null, val profileImage: String? = null)

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
) {
    fun toDomain() = Moment(
        id = id,
        eventId = eventId,
        eventTitle = eventTitle,
        authorId = authorId,
        authorNickname = authorNickname,
        media = media.map { ProfileMomentMedia(it.mediaUrl, it.mediaType, it.sortOrder) },
        caption = caption,
        visibility = visibility,
        createdAt = createdAt,
    )
}
