package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository

class UpdateMemoUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(
        groupId: String,
        memoId: String,
        title: String?,
        content: String?,
        isPinned: Boolean?,
    ): Memo = repository.updateMemo(groupId, memoId, title, content, isPinned)
}
