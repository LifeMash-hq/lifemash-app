package org.bmsk.lifemash.domain.usecase.auth

import org.bmsk.lifemash.domain.auth.AuthRepository
import org.bmsk.lifemash.domain.auth.AuthToken

class SignInWithKakaoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(accessToken: String): AuthToken =
        repository.signInWithKakao(accessToken)
}
