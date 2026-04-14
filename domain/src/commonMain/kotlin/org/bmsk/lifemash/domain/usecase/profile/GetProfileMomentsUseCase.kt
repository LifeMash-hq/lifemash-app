package org.bmsk.lifemash.domain.usecase.profile

import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.MomentRepository

class GetProfileMomentsUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(userId: String): List<Moment> =
        repository.getUserMoments(userId)
}
