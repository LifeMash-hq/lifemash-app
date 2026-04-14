package org.bmsk.lifemash.domain.moment

interface MomentRepository {
    suspend fun create(
        eventId: String?,
        caption: String?,
        visibility: Visibility,
        media: List<MomentMedia>,
    ): Moment

    suspend fun getUserMoments(userId: String): List<Moment>

    suspend fun delete(momentId: String)
}
