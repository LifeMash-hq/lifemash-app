package org.bmsk.lifemash.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.domain.auth.AuthUser

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthUser?> = repository.getCurrentUser()
}
