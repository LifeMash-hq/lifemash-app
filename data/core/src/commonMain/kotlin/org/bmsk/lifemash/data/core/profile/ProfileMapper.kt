package org.bmsk.lifemash.data.core.profile

import org.bmsk.lifemash.data.remote.profile.dto.MomentDto
import org.bmsk.lifemash.data.remote.profile.dto.MomentMediaDto
import org.bmsk.lifemash.data.remote.profile.dto.ProfileDto
import org.bmsk.lifemash.domain.moment.MediaType
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.MomentMedia
import org.bmsk.lifemash.domain.moment.Visibility
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
        caption = caption,
        visibility = runCatching { Visibility.valueOf(visibility.uppercase()) }
            .getOrDefault(Visibility.PUBLIC),
        media = media.map { it.toDomain() },
        createdAt = createdAt,
    )

private fun MomentMediaDto.toDomain(): MomentMedia =
    MomentMedia(
        mediaUrl = mediaUrl,
        mediaType = runCatching { MediaType.valueOf(mediaType.uppercase()) }
            .getOrDefault(MediaType.IMAGE),
        sortOrder = sortOrder,
    )
