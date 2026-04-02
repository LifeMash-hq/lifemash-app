package org.bmsk.lifemash.memo.data.repository

import org.bmsk.lifemash.memo.data.api.MemoApi
import org.bmsk.lifemash.memo.data.api.dto.CreateMemoBody
import org.bmsk.lifemash.memo.data.api.dto.SyncChecklistBody
import org.bmsk.lifemash.memo.data.api.dto.SyncChecklistItemEntry
import org.bmsk.lifemash.memo.data.api.dto.UpdateMemoBody
import org.bmsk.lifemash.memo.domain.model.ChecklistItem
import org.bmsk.lifemash.memo.domain.model.Memo
import org.bmsk.lifemash.memo.domain.repository.MemoRepository

internal class MemoRepositoryImpl(private val api: MemoApi) : MemoRepository {

    override suspend fun getGroupMemos(groupId: String): List<Memo> =
        api.getGroupMemos(groupId).map { it.toDomain() }

    override suspend fun getMemo(groupId: String, memoId: String): Memo =
        api.getMemo(groupId, memoId).toDomain()

    override suspend fun createMemo(
        groupId: String,
        title: String,
        content: String,
        isPinned: Boolean,
        isChecklist: Boolean,
        checklistItems: List<ChecklistItem>,
    ): Memo = api.createMemo(
        groupId,
        CreateMemoBody(
            title = title,
            content = content,
            isPinned = isPinned,
            isChecklist = isChecklist,
            checklistItems = checklistItems.map { it.toEntry() },
        ),
    ).toDomain()

    override suspend fun updateMemo(
        groupId: String,
        memoId: String,
        title: String?,
        content: String?,
        isPinned: Boolean?,
    ): Memo = api.updateMemo(
        groupId,
        memoId,
        UpdateMemoBody(title = title, content = content, isPinned = isPinned),
    ).toDomain()

    override suspend fun deleteMemo(groupId: String, memoId: String) =
        api.deleteMemo(groupId, memoId)

    override suspend fun searchMemos(groupId: String, query: String): List<Memo> =
        api.searchMemos(groupId, query).map { it.toDomain() }

    override suspend fun syncChecklist(
        groupId: String,
        memoId: String,
        items: List<ChecklistItem>,
    ): List<ChecklistItem> = api.syncChecklist(
        groupId,
        memoId,
        SyncChecklistBody(items = items.map { it.toEntry() }),
    ).map { it.toDomain() }

    private fun ChecklistItem.toEntry() = SyncChecklistItemEntry(
        id = id,
        content = content,
        isChecked = isChecked,
        sortOrder = sortOrder,
    )
}
