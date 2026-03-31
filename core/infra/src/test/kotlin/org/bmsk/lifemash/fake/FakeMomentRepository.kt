package org.bmsk.lifemash.fake

import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.moment.MomentRepository
import kotlin.uuid.Uuid

class FakeMomentRepository : MomentRepository {
    private val moments = mutableMapOf<Uuid, MomentDto>()

    override fun create(eventId: Uuid, authorId: Uuid, imageUrl: String, caption: String?, visibility: String): MomentDto {
        val id = Uuid.random()
        val moment = MomentDto(
            id = id.toString(),
            eventId = eventId.toString(),
            authorId = authorId.toString(),
            authorNickname = "User",
            imageUrl = imageUrl,
            caption = caption,
            visibility = visibility,
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
                if (viewerId != null && moment.authorId == viewerId.toString()) true
                else moment.visibility == "public"
            }
            .sortedByDescending { it.createdAt }
    }

    override fun delete(momentId: Uuid) {
        moments.remove(momentId)
    }
}
