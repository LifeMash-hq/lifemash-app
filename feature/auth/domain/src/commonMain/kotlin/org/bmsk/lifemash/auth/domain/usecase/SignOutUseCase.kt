package org.bmsk.lifemash.auth.domain.usecase

import org.bmsk.lifemash.auth.domain.repository.AuthRepository

class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}
