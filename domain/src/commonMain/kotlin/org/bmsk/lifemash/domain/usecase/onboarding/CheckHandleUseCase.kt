package org.bmsk.lifemash.domain.usecase.onboarding

import org.bmsk.lifemash.domain.onboarding.OnboardingRepository

class CheckHandleUseCase(private val repository: OnboardingRepository) {
    suspend operator fun invoke(handle: String): Boolean = repository.checkHandle(handle)
}
