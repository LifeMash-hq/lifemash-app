package org.bmsk.lifemash.data.core.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import org.bmsk.lifemash.data.remote.profile.ProfileApi
import org.bmsk.lifemash.data.remote.profile.dto.UpdateProfileRequest
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.UserProfile

internal class ProfileRepositoryImpl(
    private val api: ProfileApi,
) : ProfileRepository {

    private val cache = MutableStateFlow<UserProfile?>(null)

    override fun getProfile(userId: String): Flow<UserProfile> = flow {
        cache.value?.let { emit(it) }
        val fresh = api.getProfile(userId).toDomain()
        cache.value = fresh
        emit(fresh)
    }.distinctUntilChanged()

    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile {
        val updated = api.updateProfile(UpdateProfileRequest(nickname, bio, profileImage)).toDomain()
        cache.value = updated
        return updated
    }
}
