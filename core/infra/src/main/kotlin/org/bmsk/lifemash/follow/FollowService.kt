package org.bmsk.lifemash.follow

import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.bmsk.lifemash.plugins.BadRequestException
import kotlin.uuid.Uuid

class FollowService(
    private val followRepository: FollowRepository,
    private val notificationService: org.bmsk.lifemash.notification.NotificationService,
) {
    fun follow(followerId: Uuid, followingId: Uuid) {
        if (followerId == followingId) throw BadRequestException("자기 자신을 팔로우할 수 없습니다")
        if (followRepository.isFollowing(followerId, followingId)) return
        followRepository.follow(followerId, followingId)
        notificationService.createNotification(
            userId = followingId,
            type = "follow",
            actorId = followerId,
            targetId = followerId,
        )
    }

    fun unfollow(followerId: Uuid, followingId: Uuid) {
        followRepository.unfollow(followerId, followingId)
    }

    fun getFollowers(userId: Uuid): List<UserSummaryDto> = followRepository.getFollowers(userId)
    fun getFollowing(userId: Uuid): List<UserSummaryDto> = followRepository.getFollowing(userId)
}
