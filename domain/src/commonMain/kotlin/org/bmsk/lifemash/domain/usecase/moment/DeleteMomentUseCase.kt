package org.bmsk.lifemash.domain.usecase.moment

import org.bmsk.lifemash.domain.moment.MomentRepository

class DeleteMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(momentId: String) = repository.delete(momentId)
}
