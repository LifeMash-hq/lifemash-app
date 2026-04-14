package org.bmsk.lifemash.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.profile.Moment
import org.bmsk.lifemash.domain.profile.ProfileRepository

class GetProfileMomentsUseCase(private val repository: ProfileRepository) {
    operator fun invoke(userId: String): Flow<List<Moment>> = repository.getMoments(userId)
}
