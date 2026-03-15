package org.bmsk.lifemash.auth.domain.usecase

import org.bmsk.lifemash.auth.domain.model.AuthToken
import org.bmsk.lifemash.auth.domain.repository.AuthRepository

class SignInWithKakaoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(accessToken: String): AuthToken =
        repository.signInWithKakao(accessToken)
}
