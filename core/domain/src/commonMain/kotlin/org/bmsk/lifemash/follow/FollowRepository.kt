package org.bmsk.lifemash.follow

import org.bmsk.lifemash.model.follow.UserSummaryDto
import kotlin.uuid.Uuid

interface FollowRepository {
    fun follow(followerId: Uuid, followingId: Uuid)
    fun unfollow(followerId: Uuid, followingId: Uuid)
    fun isFollowing(followerId: Uuid, followingId: Uuid): Boolean
    fun getFollowers(userId: Uuid): List<UserSummaryDto>
    fun getFollowing(userId: Uuid): List<UserSummaryDto>
    fun getFollowingIds(userId: Uuid): List<Uuid>
}
