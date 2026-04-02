package org.bmsk.lifemash.moment.domain.repository

import org.bmsk.lifemash.moment.domain.model.Moment
import org.bmsk.lifemash.moment.domain.model.MomentMedia
import org.bmsk.lifemash.moment.domain.model.Visibility

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
