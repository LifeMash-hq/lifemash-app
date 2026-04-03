package org.bmsk.lifemash.calendar.domain.repository

import org.bmsk.lifemash.calendar.domain.model.Follower

interface FollowRepository {
    suspend fun getFollowers(userId: String): List<Follower>
    suspend fun getFollowing(userId: String): List<Follower>
}
