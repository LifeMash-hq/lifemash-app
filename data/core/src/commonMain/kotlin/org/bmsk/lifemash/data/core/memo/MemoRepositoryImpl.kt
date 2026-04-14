package org.bmsk.lifemash.data.core.memo

import org.bmsk.lifemash.domain.memo.ChecklistItem
import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository
import org.bmsk.lifemash.data.remote.memo.MemoApi
import org.bmsk.lifemash.data.remote.memo.dto.CreateMemoRequest
import org.bmsk.lifemash.data.remote.memo.dto.SyncChecklistItemEntry
import org.bmsk.lifemash.data.remote.memo.dto.SyncChecklistRequest
import org.bmsk.lifemash.data.remote.memo.dto.UpdateMemoRequest

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
        CreateMemoRequest(
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
        UpdateMemoRequest(title = title, content = content, isPinned = isPinned),
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
        SyncChecklistRequest(items = items.map { it.toEntry() }),
    ).map { it.toDomain() }

    private fun ChecklistItem.toEntry() = SyncChecklistItemEntry(
        id = id,
        content = content,
        isChecked = isChecked,
        sortOrder = sortOrder,
    )
}
