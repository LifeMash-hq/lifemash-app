package org.bmsk.lifemash.auth.domain.usecase

import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.repository.AuthRepository

class SignInWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): AuthToken =
        repository.signInWithGoogle(idToken)
}
