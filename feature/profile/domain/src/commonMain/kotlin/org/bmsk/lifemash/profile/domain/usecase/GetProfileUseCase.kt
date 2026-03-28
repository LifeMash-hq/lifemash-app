package org.bmsk.lifemash.profile.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.profile.domain.model.UserProfile
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

interface GetProfileUseCase {
    operator fun invoke(userId: String): Flow<UserProfile>
}

class GetProfileUseCaseImpl(private val repository: ProfileRepository) : GetProfileUseCase {
    override fun invoke(userId: String) = repository.getProfile(userId)
}
