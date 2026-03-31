package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object Notifications : Table("notifications") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(Users.id)
    val type = varchar("type", 30)
    val actorId = uuid("actor_id").references(Users.id).nullable()
    val targetId = uuid("target_id").nullable()
    val content = text("content").nullable()
    val isRead = bool("is_read").default(false)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, userId)
    }
}
