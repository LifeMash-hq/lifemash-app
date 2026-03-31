package org.bmsk.lifemash.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.profile.domain.model.ProfileSettings
import org.bmsk.lifemash.profile.domain.model.UserProfile

interface ProfileRepository {
    fun getProfile(userId: String): Flow<UserProfile>
    suspend fun updateProfile(nickname: String?, bio: String?, profileImage: String?): UserProfile
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
    suspend fun getProfileSettings(): ProfileSettings
    suspend fun updateProfileSettings(settings: ProfileSettings)
}
