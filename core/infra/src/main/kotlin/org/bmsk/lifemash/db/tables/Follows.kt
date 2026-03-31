package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object Follows : Table("follows") {
    val followerId = uuid("follower_id").references(Users.id)
    val followingId = uuid("following_id").references(Users.id)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(followerId, followingId)

    init {
        check("no_self_follow") { followerId neq followingId }
    }
}
