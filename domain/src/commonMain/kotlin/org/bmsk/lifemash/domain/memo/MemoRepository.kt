package org.bmsk.lifemash.domain.memo

interface MemoRepository {
    suspend fun getGroupMemos(groupId: String): List<Memo>
    suspend fun getMemo(groupId: String, memoId: String): Memo
    suspend fun createMemo(
        groupId: String,
        title: String,
        content: String,
        isPinned: Boolean,
        isChecklist: Boolean,
        checklistItems: List<ChecklistItem>,
    ): Memo
    suspend fun updateMemo(
        groupId: String,
        memoId: String,
        title: String?,
        content: String?,
        isPinned: Boolean?,
    ): Memo
    suspend fun deleteMemo(groupId: String, memoId: String)
    suspend fun searchMemos(groupId: String, query: String): List<Memo>
    suspend fun syncChecklist(
        groupId: String,
        memoId: String,
        items: List<ChecklistItem>,
    ): List<ChecklistItem>
}
