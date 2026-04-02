package org.bmsk.lifemash.memo

import org.bmsk.lifemash.db.tables.ChecklistItems
import org.bmsk.lifemash.db.tables.Memos
import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.CreateMemoRequest
import org.bmsk.lifemash.model.memo.MemoDto
import org.bmsk.lifemash.model.memo.UpdateMemoRequest
import org.bmsk.lifemash.util.nowUtc
import org.bmsk.lifemash.util.toKotlinxInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class ExposedMemoRepository : MemoRepository {

    override fun findByGroup(groupId: Uuid): List<MemoDto> = transaction {
        Memos.selectAll()
            .where { Memos.groupId eq groupId.toJavaUuid() }
            .orderBy(Memos.isPinned to SortOrder.DESC, Memos.updatedAt to SortOrder.DESC)
            .map { row -> row.toDto(fetchChecklistItems(row[Memos.id].toString())) }
    }

    override fun findById(memoId: Uuid): MemoDto? = transaction {
        Memos.selectAll()
            .where { Memos.id eq memoId.toJavaUuid() }
            .singleOrNull()
            ?.let { row -> row.toDto(fetchChecklistItems(memoId.toString())) }
    }

    override fun create(groupId: Uuid, authorId: Uuid, request: CreateMemoRequest): MemoDto = transaction {
        val now = nowUtc()
        val memoRow = Memos.insert {
            it[Memos.groupId] = groupId.toJavaUuid()
            it[Memos.authorId] = authorId.toJavaUuid()
            it[Memos.title] = request.title
            it[Memos.content] = request.content
            it[Memos.isPinned] = request.isPinned
            it[Memos.isChecklist] = request.isChecklist
            it[Memos.createdAt] = now
            it[Memos.updatedAt] = now
        }.resultedValues!!.first()

        val memoId = memoRow[Memos.id].toString()
        val items = if (request.isChecklist) {
            request.checklistItems.mapIndexed { idx, item ->
                ChecklistItems.insert {
                    it[ChecklistItems.memoId] = memoRow[Memos.id]
                    it[ChecklistItems.content] = item.content
                    it[ChecklistItems.isChecked] = item.isChecked
                    it[ChecklistItems.sortOrder] = item.sortOrder.takeIf { o -> o != 0 } ?: idx
                    it[ChecklistItems.createdAt] = now
                }.resultedValues!!.first().toItemDto()
            }
        } else emptyList()

        memoRow.toDto(items)
    }

    override fun update(memoId: Uuid, request: UpdateMemoRequest): MemoDto = transaction {
        Memos.update({ Memos.id eq memoId.toJavaUuid() }) {
            request.title?.let { v -> it[title] = v }
            request.content?.let { v -> it[content] = v }
            request.isPinned?.let { v -> it[isPinned] = v }
            it[updatedAt] = nowUtc()
        }
        findById(memoId)!!
    }

    override fun delete(memoId: Uuid) {
        transaction {
            Memos.deleteWhere { Memos.id eq memoId.toJavaUuid() }
        }
    }

    override fun search(groupId: Uuid, query: String): List<MemoDto> = transaction {
        val pattern = "%${query.lowercase()}%"
        Memos.selectAll()
            .where {
                (Memos.groupId eq groupId.toJavaUuid()) and
                    (Memos.title.lowerCase() like pattern or (Memos.content.lowerCase() like pattern))
            }
            .orderBy(Memos.updatedAt to SortOrder.DESC)
            .map { row -> row.toDto(fetchChecklistItems(row[Memos.id].toString())) }
    }

    override fun countPinned(groupId: Uuid): Int = transaction {
        Memos.selectAll()
            .where { (Memos.groupId eq groupId.toJavaUuid()) and (Memos.isPinned eq true) }
            .count()
            .toInt()
    }

    private fun fetchChecklistItems(memoId: String): List<ChecklistItemDto> =
        ChecklistItems.selectAll()
            .where { ChecklistItems.memoId eq java.util.UUID.fromString(memoId) }
            .orderBy(ChecklistItems.sortOrder)
            .map { it.toItemDto() }

    private fun ResultRow.toDto(items: List<ChecklistItemDto>) = MemoDto(
        id = this[Memos.id].toString(),
        groupId = this[Memos.groupId].toString(),
        authorId = this[Memos.authorId].toString(),
        title = this[Memos.title],
        content = this[Memos.content],
        isPinned = this[Memos.isPinned],
        isChecklist = this[Memos.isChecklist],
        checklistItems = items,
        createdAt = this[Memos.createdAt].toKotlinxInstant(),
        updatedAt = this[Memos.updatedAt].toKotlinxInstant(),
    )

    private fun ResultRow.toItemDto() = ChecklistItemDto(
        id = this[ChecklistItems.id].toString(),
        content = this[ChecklistItems.content],
        isChecked = this[ChecklistItems.isChecked],
        sortOrder = this[ChecklistItems.sortOrder],
    )
}
