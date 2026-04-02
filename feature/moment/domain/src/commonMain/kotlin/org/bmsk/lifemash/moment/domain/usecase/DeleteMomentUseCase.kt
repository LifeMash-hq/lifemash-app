package org.bmsk.lifemash.moment.domain.usecase

import org.bmsk.lifemash.moment.domain.repository.MomentRepository

class DeleteMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(momentId: String) = repository.delete(momentId)
}
