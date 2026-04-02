package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * memos 테이블 — 그룹 내 공유 메모.
 * 일반 텍스트 메모와 체크리스트 메모 두 종류를 지원한다.
 * 그룹이 삭제되면 관련 메모도 함께 삭제된다 (CASCADE).
 */
object Memos : Table("memos") {
    val id = uuid("id").autoGenerate()
    val groupId = uuid("group_id").references(Groups.id, onDelete = ReferenceOption.CASCADE)
    val authorId = uuid("author_id").references(Users.id)
    val title = varchar("title", 100)
    val content = text("content").default("")
    val isPinned = bool("is_pinned").default(false)
    val isChecklist = bool("is_checklist").default(false)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, groupId)
        index(false, groupId, isPinned)
    }
}
