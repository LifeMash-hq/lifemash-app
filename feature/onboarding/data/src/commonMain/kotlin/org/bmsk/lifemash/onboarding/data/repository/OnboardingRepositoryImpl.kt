package org.bmsk.lifemash.onboarding.data.repository

import org.bmsk.lifemash.onboarding.data.api.OnboardingApi
import org.bmsk.lifemash.onboarding.data.api.dto.UpdateProfileBody
import org.bmsk.lifemash.onboarding.domain.repository.OnboardingRepository

internal class OnboardingRepositoryImpl(
    private val api: OnboardingApi,
) : OnboardingRepository {

    override suspend fun checkHandle(handle: String): Boolean =
        api.checkHandle(handle).isAvailable

    override suspend fun saveProfile(nickname: String, username: String, birthDate: String?) {
        api.updateProfile(
            UpdateProfileBody(
                nickname = nickname,
                username = username,
                birthDate = birthDate,
            )
        )
    }
}
