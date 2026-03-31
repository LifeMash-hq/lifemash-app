package org.bmsk.lifemash.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.profile.domain.model.Moment

interface MomentRepository {
    fun getMoments(userId: String): Flow<List<Moment>>
    suspend fun postMoment(eventId: String, imageUrl: String, caption: String?, visibility: String): Moment
    suspend fun deleteMoment(momentId: String)
}
