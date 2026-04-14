package org.bmsk.lifemash.data.core.feed

import org.bmsk.lifemash.data.remote.feed.dto.FeedCommentDto
import org.bmsk.lifemash.data.remote.feed.dto.FeedMediaDto
import org.bmsk.lifemash.data.remote.feed.dto.FeedPageDto
import org.bmsk.lifemash.data.remote.feed.dto.FeedPostDto
import org.bmsk.lifemash.domain.feed.FeedComment
import org.bmsk.lifemash.domain.feed.FeedMedia
import org.bmsk.lifemash.domain.feed.FeedPage
import org.bmsk.lifemash.domain.feed.FeedPost

internal fun FeedPageDto.toDomain(): FeedPage =
    FeedPage(
        items = items.map { it.toDomain() },
        nextCursor = nextCursor,
    )

internal fun FeedPostDto.toDomain(): FeedPost =
    FeedPost(
        id = id,
        authorId = authorId,
        authorNickname = authorNickname,
        authorProfileImage = authorProfileImage,
        eventId = eventId,
        eventTitle = eventTitle,
        eventDate = eventDate,
        media = media.map { it.toDomain() },
        caption = caption,
        previewComments = previewComments.map { it.toDomain() },
        likeCount = likeCount,
        isLiked = isLiked,
        commentCount = commentCount,
        createdAt = createdAt,
    )

internal fun FeedMediaDto.toDomain(): FeedMedia =
    FeedMedia(
        mediaUrl = mediaUrl,
        mediaType = mediaType,
        sortOrder = sortOrder,
        width = width,
        height = height,
        durationMs = durationMs,
    )

internal fun FeedCommentDto.toDomain(): FeedComment =
    FeedComment(
        id = id,
        authorId = authorId,
        authorNickname = authorNickname,
        authorProfileImage = authorProfileImage,
        content = content,
        createdAt = createdAt,
    )
