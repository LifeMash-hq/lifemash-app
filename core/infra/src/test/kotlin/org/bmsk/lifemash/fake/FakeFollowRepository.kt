package org.bmsk.lifemash.fake

import org.bmsk.lifemash.follow.FollowRepository
import org.bmsk.lifemash.model.follow.UserSummaryDto
import kotlin.uuid.Uuid

class FakeFollowRepository : FollowRepository {
    private val follows = mutableSetOf<Pair<Uuid, Uuid>>() // (followerId, followingId)
    private val users = mutableMapOf<Uuid, UserSummaryDto>()

    fun addUser(id: Uuid, nickname: String, profileImage: String? = null) {
        users[id] = UserSummaryDto(id.toString(), nickname, profileImage)
    }

    override fun follow(followerId: Uuid, followingId: Uuid) {
        follows.add(followerId to followingId)
    }

    override fun unfollow(followerId: Uuid, followingId: Uuid) {
        follows.remove(followerId to followingId)
    }

    override fun isFollowing(followerId: Uuid, followingId: Uuid): Boolean {
        return (followerId to followingId) in follows
    }

    override fun getFollowers(userId: Uuid): List<UserSummaryDto> {
        return follows.filter { it.second == userId }.mapNotNull { users[it.first] }
    }

    override fun getFollowing(userId: Uuid): List<UserSummaryDto> {
        return follows.filter { it.first == userId }.mapNotNull { users[it.second] }
    }

    override fun getFollowingIds(userId: Uuid): List<Uuid> {
        return follows.filter { it.first == userId }.map { it.second }
    }
}
