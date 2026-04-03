package org.bmsk.lifemash.onboarding.domain.repository

interface OnboardingRepository {
    suspend fun checkHandle(handle: String): Boolean
    suspend fun saveProfile(nickname: String, username: String, birthDate: String?)
}
