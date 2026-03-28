package org.bmsk.lifemash.profile.domain.usecase

import org.bmsk.lifemash.profile.domain.repository.ProfileRepository

interface FollowUseCase {
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
}

class FollowUseCaseImpl(private val repository: ProfileRepository) : FollowUseCase {
    override suspend fun follow(userId: String) = repository.follow(userId)
    override suspend fun unfollow(userId: String) = repository.unfollow(userId)
}
