package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository

class GetGroupMemosUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(groupId: String): List<Memo> = repository.getGroupMemos(groupId)
}
