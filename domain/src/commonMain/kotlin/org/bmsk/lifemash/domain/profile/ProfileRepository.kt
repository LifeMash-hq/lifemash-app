package org.bmsk.lifemash.domain.profile

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(userId: String): Flow<UserProfile>
    suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
    fun getMoments(userId: String): Flow<List<Moment>>
    suspend fun getProfileSettings(): ProfileSettings
    suspend fun updateProfileSettings(settings: ProfileSettings)
}
