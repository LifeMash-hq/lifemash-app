package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.moment.MomentRepository
import kotlin.uuid.Uuid

class FakeMomentRepository : MomentRepository {
    private val moments = mutableMapOf<Uuid, MomentDto>()

    override fun create(authorId: Uuid, request: CreateMomentRequest): MomentDto {
        val id = Uuid.random()
        val moment = MomentDto(
            id = id.toString(),
            eventId = request.eventId,
            eventTitle = null,
            authorId = authorId.toString(),
            authorNickname = "User",
            caption = request.caption,
            visibility = request.visibility,
            media = request.media,
            createdAt = "2026-01-01T00:00:00Z",
        )
        moments[id] = moment
        return moment
    }

    override fun findById(momentId: Uuid): MomentDto? = moments[momentId]

    override fun findByUser(userId: Uuid, viewerId: Uuid?): List<MomentDto> {
        return moments.values
            .filter { it.authorId == userId.toString() }
            .filter { moment ->
                val isSelf = viewerId != null && moment.authorId == viewerId.toString()
                if (isSelf) true
                else moment.visibility == "public" || moment.visibility == "followers"
            }
            .sortedByDescending { it.createdAt }
    }

    override fun delete(momentId: Uuid) {
        moments.remove(momentId)
    }
}
