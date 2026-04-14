package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.MemoRepository

class DeleteMemoUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(groupId: String, memoId: String) =
        repository.deleteMemo(groupId, memoId)
}
