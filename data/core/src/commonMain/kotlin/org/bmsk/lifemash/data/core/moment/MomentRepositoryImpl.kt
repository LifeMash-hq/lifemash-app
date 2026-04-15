package org.bmsk.lifemash.data.core.moment

import org.bmsk.lifemash.domain.moment.MediaType
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.MomentMedia
import org.bmsk.lifemash.domain.moment.MomentRepository
import org.bmsk.lifemash.domain.moment.Visibility
import org.bmsk.lifemash.data.remote.moment.MomentApi
import org.bmsk.lifemash.data.remote.moment.dto.CreateMomentRequest
import org.bmsk.lifemash.data.remote.moment.dto.MediaItemResponse
import org.bmsk.lifemash.data.remote.moment.dto.MomentResponse

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
        return api.create(request).toDomainModel()
    }

    override suspend fun getUserMoments(userId: String): List<Moment> =
        api.getUserMoments(userId).map { it.toDomainModel() }

    override suspend fun delete(momentId: String) {
        api.delete(momentId)
    }

    private fun MomentMedia.toDto() = MediaItemResponse(
        mediaUrl = mediaUrl,
        mediaType = mediaType.value,
        sortOrder = sortOrder,
        width = width,
        height = height,
        durationMs = durationMs,
    )

    private fun MediaItemResponse.toDomainModel() = MomentMedia(
        mediaUrl = mediaUrl,
        mediaType = if (mediaType == "video") MediaType.VIDEO else MediaType.IMAGE,
        sortOrder = sortOrder,
        width = width,
        height = height,
        durationMs = durationMs,
    )

    private fun MomentResponse.toDomainModel() = Moment(
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
        media = media.map { it.toDomainModel() },
        createdAt = createdAt,
    )
}
