package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * comments 테이블 — 일정(Event)에 달린 댓글.
 * 일정이 삭제되면 관련 댓글도 함께 삭제된다 (CASCADE).
 */
object Comments : Table("comments") {
    val id = uuid("id").autoGenerate()
    val eventId = uuid("event_id").references(Events.id, onDelete = ReferenceOption.CASCADE) // 어떤 일정의 댓글인지
    val authorId = uuid("author_id").references(Users.id)  // 댓글 작성자
    val content = text("content")                           // 댓글 내용
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        // eventId 인덱스: 특정 일정의 댓글 목록을 빠르게 조회하기 위함
        index(false, eventId)
    }
}
