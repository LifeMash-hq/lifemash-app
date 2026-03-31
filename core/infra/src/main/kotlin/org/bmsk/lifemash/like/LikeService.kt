package org.bmsk.lifemash.like

import kotlin.uuid.Uuid

class LikeService(
    private val likeRepository: LikeRepository,
) {
    fun like(userId: Uuid, momentId: Uuid) {
        if (likeRepository.isLiked(userId, momentId)) return
        likeRepository.like(userId, momentId)
    }

    fun unlike(userId: Uuid, momentId: Uuid) {
        likeRepository.unlike(userId, momentId)
    }

    fun getLikeCount(momentId: Uuid): Int = likeRepository.getLikeCount(momentId)
}
