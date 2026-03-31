package org.bmsk.lifemash.fake

import org.bmsk.lifemash.like.LikeRepository
import kotlin.uuid.Uuid

class FakeLikeRepository : LikeRepository {
    private val likes = mutableSetOf<Pair<Uuid, Uuid>>()

    override fun like(userId: Uuid, momentId: Uuid) {
        likes.add(userId to momentId)
    }

    override fun unlike(userId: Uuid, momentId: Uuid) {
        likes.remove(userId to momentId)
    }

    override fun isLiked(userId: Uuid, momentId: Uuid): Boolean = (userId to momentId) in likes

    override fun getLikeCount(momentId: Uuid): Int = likes.count { it.second == momentId }
}
