package org.bmsk.lifemash.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.UserProfile

class GetUserProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(userId: String): Flow<UserProfile> = repository.getProfile(userId)
}
