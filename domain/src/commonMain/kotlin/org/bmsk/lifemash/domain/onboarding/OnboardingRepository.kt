package org.bmsk.lifemash.domain.onboarding

interface OnboardingRepository {
    suspend fun checkHandle(handle: String): Boolean
    suspend fun saveProfile(
        nickname: String,
        username: String,
        birthDate: String?,
    )
}
