package org.bmsk.lifemash.domain.usecase.memo

import org.bmsk.lifemash.domain.memo.Memo
import org.bmsk.lifemash.domain.memo.MemoRepository

class SearchMemosUseCase(private val repository: MemoRepository) {
    suspend operator fun invoke(groupId: String, query: String): List<Memo> =
        repository.searchMemos(groupId, query)
}
