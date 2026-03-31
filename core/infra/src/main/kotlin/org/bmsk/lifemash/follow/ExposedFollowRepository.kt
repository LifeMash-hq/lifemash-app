package org.bmsk.lifemash.follow

import org.bmsk.lifemash.db.tables.Follows
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.follow.UserSummaryDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class ExposedFollowRepository : FollowRepository {

    override fun follow(followerId: Uuid, followingId: Uuid): Unit = transaction {
        Follows.insertIgnore {
            it[Follows.followerId] = followerId.toJavaUuid()
            it[Follows.followingId] = followingId.toJavaUuid()
            it[Follows.createdAt] = nowUtc()
        }
    }

    override fun unfollow(followerId: Uuid, followingId: Uuid): Unit = transaction {
        val javaFollowerId = followerId.toJavaUuid()
        val javaFollowingId = followingId.toJavaUuid()
        Follows.deleteWhere {
            (Follows.followerId eq javaFollowerId) and
                (Follows.followingId eq javaFollowingId)
        }
    }

    override fun isFollowing(followerId: Uuid, followingId: Uuid): Boolean = transaction {
        Follows.selectAll().where {
            (Follows.followerId eq followerId.toJavaUuid()) and
                (Follows.followingId eq followingId.toJavaUuid())
        }.count() > 0
    }

    override fun getFollowers(userId: Uuid): List<UserSummaryDto> = transaction {
        Follows.join(Users, JoinType.INNER, Follows.followerId, Users.id)
            .selectAll()
            .where { Follows.followingId eq userId.toJavaUuid() }
            .map { it.toUserSummary() }
    }

    override fun getFollowing(userId: Uuid): List<UserSummaryDto> = transaction {
        Follows.join(Users, JoinType.INNER, Follows.followingId, Users.id)
            .selectAll()
            .where { Follows.followerId eq userId.toJavaUuid() }
            .map { it.toUserSummary() }
    }

    override fun getFollowingIds(userId: Uuid): List<Uuid> = transaction {
        Follows.selectAll()
            .where { Follows.followerId eq userId.toJavaUuid() }
            .map { it[Follows.followingId].toKotlinUuid() }
    }

    /** 순수 매핑 — ResultRow → UserSummaryDto */
    private fun ResultRow.toUserSummary() = UserSummaryDto(
        id = this[Users.id].toString(),
        nickname = this[Users.nickname],
        profileImage = this[Users.profileImage],
    )
}
