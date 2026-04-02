package org.bmsk.lifemash.moment.data.repository

import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MediaItemDto
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.moment.data.api.MomentApi
import org.bmsk.lifemash.moment.domain.model.MediaType
import org.bmsk.lifemash.moment.domain.model.Moment
import org.bmsk.lifemash.moment.domain.model.MomentMedia
import org.bmsk.lifemash.moment.domain.model.Visibility
import org.bmsk.lifemash.moment.domain.repository.MomentRepository

internal class MomentRepositoryImpl(private val api: MomentApi) : MomentRepository {

    override suspend fun create(
        eventId: String?,
        caption: String?,
        visibility: Visibility,
        media: List<MomentMedia>,
    ): Moment {
        val request = CreateMomentRequest(
            eventId = eventId,
            caption = caption,
            visibility = visibility.value,
            media = media.map { it.toDto() },
        )
        return api.create(request).toDomain()
    }

    override suspend fun getUserMoments(userId: String): List<Moment> =
        api.getUserMoments(userId).map { it.toDomain() }

    override suspend fun delete(momentId: String) =
        api.delete(momentId)

    // ── mapping ──

    private fun MomentMedia.toDto() = MediaItemDto(
        mediaUrl = mediaUrl,
        mediaType = mediaType.value,
        sortOrder = sortOrder,
        width = width,
        height = height,
        durationMs = durationMs,
    )

    private fun MediaItemDto.toDomain() = MomentMedia(
        mediaUrl = mediaUrl,
        mediaType = if (mediaType == "video") MediaType.VIDEO else MediaType.IMAGE,
        sortOrder = sortOrder,
        width = width,
        height = height,
        durationMs = durationMs,
    )

    private fun MomentDto.toDomain() = Moment(
        id = id,
        eventId = eventId,
        eventTitle = eventTitle,
        authorId = authorId,
        authorNickname = authorNickname,
        authorProfileImage = authorProfileImage,
        caption = caption,
        visibility = when (visibility) {
            "followers" -> Visibility.FOLLOWERS
            "private" -> Visibility.PRIVATE
            else -> Visibility.PUBLIC
        },
        media = media.map { it.toDomain() },
        createdAt = createdAt,
    )
}
