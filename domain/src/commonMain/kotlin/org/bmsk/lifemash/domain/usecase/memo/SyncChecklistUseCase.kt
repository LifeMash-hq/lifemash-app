package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.ChecklistItem
import org.bmsk.lifemash.domain.memo.MemoRepository

class SyncChecklistUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(
        groupId: String,
        memoId: String,
        items: List<ChecklistItem>,
    ): List<ChecklistItem> = repository.syncChecklist(groupId, memoId, items)
}
