package org.bmsk.lifemash.moment.domain.usecase

import org.bmsk.lifemash.moment.domain.model.Moment
import org.bmsk.lifemash.moment.domain.repository.MomentRepository

class GetUserMomentsUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(userId: String): List<Moment> =
        repository.getUserMoments(userId)
}
