package org.bmsk.lifemash.domain.usecase.auth

import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.domain.auth.AuthToken

class SignInWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthToken =
        repository.signInWithEmail(email, password)
}
