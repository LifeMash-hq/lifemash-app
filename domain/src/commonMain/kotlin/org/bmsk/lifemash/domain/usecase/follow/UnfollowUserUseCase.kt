package org.bmsk.lifemash.domain.usecase.follow

import org.bmsk.lifemash.domain.profile.ProfileRepository

class UnfollowUserUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String) = repository.unfollow(userId)
}
