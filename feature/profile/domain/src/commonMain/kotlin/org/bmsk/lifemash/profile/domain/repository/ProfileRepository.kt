package org.bmsk.lifemash.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.UserProfile

interface ProfileRepository {
    fun getProfile(userId: String): Flow<UserProfile>
    suspend fun updateProfile(nickname: String?, bio: String?, profileImage: String?): UserProfile
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
    fun getMoments(userId: String): Flow<List<Moment>>
    suspend fun postMoment(eventId: String, imageUrl: String, caption: String?, visibility: String): Moment
    suspend fun deleteMoment(momentId: String)
}
