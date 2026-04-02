package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object Moments : Table("moments") {
    val id = uuid("id").autoGenerate()
    val eventId = uuid("event_id").references(Events.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val authorId = uuid("author_id").references(Users.id)
    val caption = varchar("caption", 200).nullable()
    val visibility = varchar("visibility", 20).default("public")
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}
