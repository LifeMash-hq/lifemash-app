package org.bmsk.lifemash.domain.usecase.moment

import org.bmsk.lifemash.domain.moment.MomentRepository
import org.bmsk.lifemash.domain.moment.Moment

class GetUserMomentsUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(userId: String): List<Moment> =
        repository.getUserMoments(userId)
}
