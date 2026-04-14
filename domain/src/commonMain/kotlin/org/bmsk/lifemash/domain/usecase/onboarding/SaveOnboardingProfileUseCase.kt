package org.bmsk.lifemash.domain.usecase.onboarding

import org.bmsk.lifemash.domain.onboarding.OnboardingRepository

class SaveOnboardingProfileUseCase(private val repository: OnboardingRepository) {
    suspend operator fun invoke(nickname: String, username: String, birthDate: String?) =
        repository.saveProfile(nickname, username, birthDate)
}
