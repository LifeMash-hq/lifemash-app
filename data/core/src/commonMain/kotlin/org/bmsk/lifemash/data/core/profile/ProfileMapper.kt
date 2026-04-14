package org.bmsk.lifemash.data.core.profile

import org.bmsk.lifemash.data.remote.profile.dto.MomentDto
import org.bmsk.lifemash.data.remote.profile.dto.MomentMediaDto
import org.bmsk.lifemash.data.remote.profile.dto.ProfileDto
import org.bmsk.lifemash.domain.profile.Moment
import org.bmsk.lifemash.domain.profile.ProfileMomentMedia
import org.bmsk.lifemash.domain.profile.UserProfile

internal fun ProfileDto.toDomain(isFollowing: Boolean = false): UserProfile =
    UserProfile(
        id = id,
        email = email,
        nickname = nickname,
        bio = bio,
        profileImage = profileImage,
        followerCount = followerCount,
        followingCount = followingCount,
        isFollowing = isFollowing,
    )

internal fun MomentDto.toDomain(): Moment =
    Moment(
        id = id,
        eventId = eventId,
        eventTitle = eventTitle,
        authorId = authorId,
        authorNickname = authorNickname,
        media = media.map { it.toDomain() },
        caption = caption,
        visibility = visibility,
        createdAt = createdAt,
    )

private fun MomentMediaDto.toDomain(): ProfileMomentMedia =
    ProfileMomentMedia(
        mediaUrl = mediaUrl,
        mediaType = mediaType,
        sortOrder = sortOrder,
    )
