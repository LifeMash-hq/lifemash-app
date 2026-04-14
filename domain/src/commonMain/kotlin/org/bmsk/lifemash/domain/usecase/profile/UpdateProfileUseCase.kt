package org.bmsk.lifemash.domain.usecase.profile

import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.UserProfile

class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfile = repository.updateProfile(nickname, bio, profileImage)
}
