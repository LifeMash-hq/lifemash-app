package org.bmsk.lifemash.domain.calendar

interface FollowRepository {
    suspend fun getFollowers(userId: String): List<Follower>
    suspend fun getFollowing(userId: String): List<Follower>
    suspend fun follow(userId: String)
    suspend fun unfollow(userId: String)
}
