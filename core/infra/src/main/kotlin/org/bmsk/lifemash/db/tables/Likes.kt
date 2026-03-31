package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object Likes : Table("likes") {
    val userId = uuid("user_id").references(Users.id)
    val momentId = uuid("moment_id").references(Moments.id)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(userId, momentId)
}
