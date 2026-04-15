package org.bmsk.lifemash.data.core.calendar

import org.bmsk.lifemash.data.remote.calendar.FollowApi
import org.bmsk.lifemash.domain.calendar.FollowRepository
import org.bmsk.lifemash.domain.calendar.Follower

internal class FollowRepositoryImpl(private val api: FollowApi) : FollowRepository {

    override suspend fun getFollowers(userId: String): List<Follower> = api.getFollowers(userId).map { it.toDomain() }

    override suspend fun getFollowing(userId: String): List<Follower> = api.getFollowing(userId).map { it.toDomain() }

    override suspend fun follow(userId: String) {
        api.follow(userId)
    }

    override suspend fun unfollow(userId: String) {
        api.unfollow(userId)
    }
}
