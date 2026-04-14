package org.bmsk.lifemash.data.core.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.domain.profile.Moment
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.UserProfile
import org.bmsk.lifemash.data.remote.profile.ProfileApi
import org.bmsk.lifemash.data.remote.profile.dto.UpdateProfileRequest

internal class ProfileRepositoryImpl(private val api: ProfileApi) : ProfileRepository {
    private var cachedSettings = ProfileSettings()

    override fun getProfile(userId: String): Flow<UserProfile> = flow {
        emit(api.getProfile(userId).toDomain())
    }

    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile =
        api.updateProfile(UpdateProfileRequest(nickname, bio, profileImage)).toDomain()

    override suspend fun follow(userId: String) = api.follow(userId)
    override suspend fun unfollow(userId: String) = api.unfollow(userId)

    override fun getMoments(userId: String): Flow<List<Moment>> = flow {
        emit(api.getMoments(userId).map { it.toDomain() })
    }

    override suspend fun getProfileSettings(): ProfileSettings = cachedSettings

    override suspend fun updateProfileSettings(settings: ProfileSettings) {
        cachedSettings = settings
    }
}
