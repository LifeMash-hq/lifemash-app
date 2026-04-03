package org.bmsk.lifemash.calendar.data.repository

import org.bmsk.lifemash.calendar.data.api.FollowApi
import org.bmsk.lifemash.calendar.domain.model.Follower
import org.bmsk.lifemash.calendar.domain.repository.FollowRepository

internal class FollowRepositoryImpl(private val api: FollowApi) : FollowRepository {

    override suspend fun getFollowers(userId: String): List<Follower> =
        api.getFollowers(userId).map { it.toDomain() }

    override suspend fun getFollowing(userId: String): List<Follower> =
        api.getFollowing(userId).map { it.toDomain() }
}
