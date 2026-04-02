package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * checklist_items 테이블 — 체크리스트 메모의 개별 항목.
 * 메모가 삭제되면 관련 항목도 함께 삭제된다 (CASCADE).
 */
object ChecklistItems : Table("checklist_items") {
    val id = uuid("id").autoGenerate()
    val memoId = uuid("memo_id").references(Memos.id, onDelete = ReferenceOption.CASCADE)
    val content = varchar("content", 200)
    val isChecked = bool("is_checked").default(false)
    val sortOrder = integer("sort_order").default(0)
    val createdAt = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, memoId)
    }
}
