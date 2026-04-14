package org.bmsk.lifemash.memo.impl

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.domain.memo.Memo

internal data class MemoUiState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val memos: PersistentList<Memo>,
    val selectedMemo: Memo?,
    val overlay: MemoOverlay,
    val searchQuery: String,
    val isCreating: Boolean,
    val isUpdating: Boolean,
) {
    companion object {
        val Default = MemoUiState(
            isLoading = true,
            errorMessage = null,
            memos = persistentListOf(),
            selectedMemo = null,
            overlay = MemoOverlay.None,
            searchQuery = "",
            isCreating = false,
            isUpdating = false,
        )
    }
}
