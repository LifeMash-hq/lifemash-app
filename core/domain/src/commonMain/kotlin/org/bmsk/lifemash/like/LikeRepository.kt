package org.bmsk.lifemash.like

import kotlin.uuid.Uuid

interface LikeRepository {
    fun like(userId: Uuid, momentId: Uuid)
    fun unlike(userId: Uuid, momentId: Uuid)
    fun isLiked(userId: Uuid, momentId: Uuid): Boolean
    fun getLikeCount(momentId: Uuid): Int
}
