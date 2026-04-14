package org.bmsk.lifemash.domain.usecase.follow

import org.bmsk.lifemash.domain.calendar.Follower
import org.bmsk.lifemash.domain.calendar.FollowRepository

class GetFollowersUseCase(private val repository: FollowRepository) {
    suspend operator fun invoke(userId: String): List<Follower> = repository.getFollowers(userId)
}
