package org.bmsk.lifemash.profile.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.profile.domain.model.Moment
import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

interface GetMomentsUseCase {
    operator fun invoke(userId: String): Flow<List<Moment>>
}

class GetMomentsUseCaseImpl(private val repository: ProfileRepository) : GetMomentsUseCase {
    override fun invoke(userId: String) = repository.getMoments(userId)
}
