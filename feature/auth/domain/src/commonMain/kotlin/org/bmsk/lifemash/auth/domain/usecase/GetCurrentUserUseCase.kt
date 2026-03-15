package org.bmsk.lifemash.auth.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.auth.domain.model.AuthUser
import org.bmsk.lifemash.auth.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthUser?> = repository.getCurrentUser()
}
