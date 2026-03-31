package org.bmsk.lifemash.like

import org.bmsk.lifemash.db.tables.Likes
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedLikeRepository : LikeRepository {

    override fun like(userId: Uuid, momentId: Uuid): Unit = transaction {
        Likes.insertIgnore {
            it[Likes.userId] = userId.toJavaUuid()
            it[Likes.momentId] = momentId.toJavaUuid()
            it[Likes.createdAt] = nowUtc()
        }
    }

    override fun unlike(userId: Uuid, momentId: Uuid): Unit = transaction {
        val javaUserId = userId.toJavaUuid()
        val javaMomentId = momentId.toJavaUuid()
        Likes.deleteWhere {
            (Likes.userId eq javaUserId) and (Likes.momentId eq javaMomentId)
        }
    }

    override fun isLiked(userId: Uuid, momentId: Uuid): Boolean = transaction {
        Likes.selectAll().where {
            (Likes.userId eq userId.toJavaUuid()) and
                (Likes.momentId eq momentId.toJavaUuid())
        }.count() > 0
    }

    override fun getLikeCount(momentId: Uuid): Int = transaction {
        Likes.selectAll().where { Likes.momentId eq momentId.toJavaUuid() }.count().toInt()
    }
}
