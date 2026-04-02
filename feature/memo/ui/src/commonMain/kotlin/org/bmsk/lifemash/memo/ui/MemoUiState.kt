package org.bmsk.lifemash.memo.ui

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.bmsk.lifemash.memo.domain.model.Memo

internal data class MemoUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val memos: PersistentList<Memo> = persistentListOf(),
    val selectedMemo: Memo? = null,
    val overlay: MemoOverlay = MemoOverlay.None,
    val searchQuery: String = "",
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
)
