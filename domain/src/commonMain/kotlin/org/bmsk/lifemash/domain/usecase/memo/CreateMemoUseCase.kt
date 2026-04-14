package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.ChecklistItem
import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository

class CreateMemoUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(
        groupId: String,
        title: String,
        content: String,
        isPinned: Boolean,
        isChecklist: Boolean,
        checklistItems: List<ChecklistItem>,
    ): Memo = repository.createMemo(groupId, title, content, isPinned, isChecklist, checklistItems)
}
