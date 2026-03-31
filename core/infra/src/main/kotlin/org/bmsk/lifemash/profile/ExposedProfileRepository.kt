package org.bmsk.lifemash.profile

import org.bmsk.lifemash.db.tables.Follows
import org.bmsk.lifemash.db.tables.Users
import org.bmsk.lifemash.model.profile.UserProfileDto
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedProfileRepository : ProfileRepository {

    override fun getProfile(userId: Uuid): UserProfileDto? = transaction {
        val javaId = userId.toJavaUuid()

        Users.selectAll().where { Users.id eq javaId }
            .singleOrNull()
            ?.toDto(
                followerCount = countFollowers(javaId),
                followingCount = countFollowing(javaId),
            )
    }

    override fun updateProfile(
        userId: Uuid,
        nickname: String?,
        bio: String?,
        profileImage: String?,
    ): UserProfileDto? = transaction {
        val javaId = userId.toJavaUuid()
        val updated = Users.update({ Users.id eq javaId }) {
            if (nickname != null) it[Users.nickname] = nickname
            if (bio != null) it[Users.bio] = bio
            if (profileImage != null) it[Users.profileImage] = profileImage
            it[Users.updatedAt] = nowUtc()
        }
        if (updated == 0) return@transaction null

        Users.selectAll().where { Users.id eq javaId }
            .singleOrNull()
            ?.toDto(
                followerCount = countFollowers(javaId),
                followingCount = countFollowing(javaId),
            )
    }

    override fun getFollowerCount(userId: Uuid): Int = transaction {
        countFollowers(userId.toJavaUuid())
    }

    override fun getFollowingCount(userId: Uuid): Int = transaction {
        countFollowing(userId.toJavaUuid())
    }

    // ── private helpers ──

    private fun countFollowers(javaId: java.util.UUID): Int =
        Follows.selectAll().where { Follows.followingId eq javaId }.count().toInt()

    private fun countFollowing(javaId: java.util.UUID): Int =
        Follows.selectAll().where { Follows.followerId eq javaId }.count().toInt()

    /** 순수 매핑 — DB 접근 없이 ResultRow → DTO 변환 */
    private fun ResultRow.toDto(followerCount: Int, followingCount: Int) = UserProfileDto(
        id = this[Users.id].toString(),
        email = this[Users.email],
        nickname = this[Users.nickname],
        bio = this[Users.bio],
        profileImage = this[Users.profileImage],
        provider = this[Users.provider],
        followerCount = followerCount,
        followingCount = followingCount,
    )
}
