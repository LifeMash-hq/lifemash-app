package org.bmsk.lifemash.memo.impl

import org.bmsk.lifemash.domain.memo.Memo

internal sealed interface MemoOverlay {
    data object None : MemoOverlay
    data class Create(val isChecklist: Boolean = false) : MemoOverlay
    data class Detail(val memo: Memo) : MemoOverlay
    data class Edit(val memo: Memo) : MemoOverlay
}
