package org.bmsk.lifemash.domain.profile

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(userId: String): Flow<UserProfile>
    suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile
}
