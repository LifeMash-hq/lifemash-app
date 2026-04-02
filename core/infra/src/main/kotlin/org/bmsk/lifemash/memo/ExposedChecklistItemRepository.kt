package org.bmsk.lifemash.memo

import org.bmsk.lifemash.db.tables.ChecklistItems
import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.SyncChecklistItemEntry
import org.bmsk.lifemash.util.nowUtc
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedChecklistItemRepository : ChecklistItemRepository {

    override fun findByMemo(memoId: Uuid): List<ChecklistItemDto> = transaction {
        ChecklistItems.selectAll()
            .where { ChecklistItems.memoId eq memoId.toJavaUuid() }
            .orderBy(ChecklistItems.sortOrder)
            .map { it.toDto() }
    }

    override fun sync(memoId: Uuid, items: List<SyncChecklistItemEntry>): List<ChecklistItemDto> = transaction {
        val javaId = memoId.toJavaUuid()
        val now = nowUtc()

        val incomingIds = items.mapNotNull { it.id?.let { id -> java.util.UUID.fromString(id) } }.toSet()
        val existingIds = ChecklistItems.selectAll()
            .where { ChecklistItems.memoId eq javaId }
            .map { it[ChecklistItems.id] }
            .toSet()

        // 더 이상 필요 없는 항목 삭제
        val toDelete = existingIds - incomingIds
        toDelete.forEach { deleteId ->
            ChecklistItems.deleteWhere { ChecklistItems.id eq deleteId }
        }

        // 각 항목 upsert
        items.forEach { entry ->
            val entryId = entry.id?.let { java.util.UUID.fromString(it) }
            if (entryId != null && entryId in existingIds) {
                ChecklistItems.update({ ChecklistItems.id eq entryId }) {
                    it[content] = entry.content
                    it[isChecked] = entry.isChecked
                    it[sortOrder] = entry.sortOrder
                }
            } else {
                ChecklistItems.insert {
                    it[ChecklistItems.memoId] = javaId
                    it[content] = entry.content
                    it[isChecked] = entry.isChecked
                    it[sortOrder] = entry.sortOrder
                    it[createdAt] = now
                }
            }
        }

        ChecklistItems.selectAll()
            .where { ChecklistItems.memoId eq javaId }
            .orderBy(ChecklistItems.sortOrder)
            .map { it.toDto() }
    }

    private fun ResultRow.toDto() = ChecklistItemDto(
        id = this[ChecklistItems.id].toString(),
        content = this[ChecklistItems.content],
        isChecked = this[ChecklistItems.isChecked],
        sortOrder = this[ChecklistItems.sortOrder],
    )
}
