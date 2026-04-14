package org.bmsk.lifemash.data.core.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.data.remote.profile.ProfileApi
import org.bmsk.lifemash.data.remote.profile.dto.UpdateProfileRequest
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.UserProfile

internal class ProfileRepositoryImpl(
    private val api: ProfileApi,
) : ProfileRepository {

    override fun getProfile(userId: String): Flow<UserProfile> = flow {
        emit(api.getProfile(userId).toDomain())
    }

    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile =
        api.updateProfile(UpdateProfileRequest(nickname, bio, profileImage)).toDomain()
}
