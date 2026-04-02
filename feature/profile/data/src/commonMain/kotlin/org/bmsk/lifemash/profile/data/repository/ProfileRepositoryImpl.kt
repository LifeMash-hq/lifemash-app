package org.bmsk.lifemash.profile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.profile.data.api.ProfileApi
import org.bmsk.lifemash.profile.data.api.dto.UpdateProfileBody
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.model.ProfileSettings
import org.bmsk.lifemash.profile.domain.model.UserProfile
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

internal class ProfileRepositoryImpl(private val api: ProfileApi) : ProfileRepository {
    private var cachedSettings = ProfileSettings()

    override fun getProfile(userId: String): Flow<UserProfile> = flow {
        emit(api.getProfile(userId).toDomain())
    }

    override suspend fun updateProfile(nickname: String?, bio: String?, profileImage: String?): UserProfile =
        api.updateProfile(UpdateProfileBody(nickname, bio, profileImage)).toDomain()

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
