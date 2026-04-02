package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MomentMedia : Table("moment_media") {
    val id = uuid("id").autoGenerate()
    val momentId = uuid("moment_id").references(Moments.id, onDelete = ReferenceOption.CASCADE)
    val mediaUrl = text("media_url")
    val mediaType = varchar("media_type", 10)  // "image" | "video"
    val sortOrder = integer("sort_order")
    val width = integer("width").nullable()
    val height = integer("height").nullable()
    val durationMs = long("duration_ms").nullable()  // 동영상 전용 (ms)

    override val primaryKey = PrimaryKey(id)
}
