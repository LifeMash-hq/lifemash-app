package org.bmsk.lifemash.domain.usecase.follow

import org.bmsk.lifemash.domain.calendar.FollowRepository

class FollowUserUseCase(private val repository: FollowRepository) {
    suspend operator fun invoke(userId: String) = repository.follow(userId)
}
