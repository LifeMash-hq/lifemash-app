package org.bmsk.lifemash.data.core.onboarding

import org.bmsk.lifemash.domain.onboarding.OnboardingRepository
import org.bmsk.lifemash.data.remote.onboarding.OnboardingApi
import org.bmsk.lifemash.data.remote.onboarding.dto.UpdateProfileRequest

internal class OnboardingRepositoryImpl(
    private val api: OnboardingApi,
) : OnboardingRepository {

    override suspend fun checkHandle(handle: String): Boolean =
        api.checkHandle(handle).isAvailable

    override suspend fun saveProfile(
        nickname: String,
        username: String,
        birthDate: String?,
    ) {
        api.updateProfile(
            UpdateProfileRequest(
                nickname = nickname,
                username = username,
                birthDate = birthDate,
            )
        )
    }
}
