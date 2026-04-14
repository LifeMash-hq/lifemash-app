package org.bmsk.lifemash.domain.usecase.auth

import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.domain.auth.AuthToken

class SignInWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): AuthToken =
        repository.signInWithGoogle(idToken)
}
