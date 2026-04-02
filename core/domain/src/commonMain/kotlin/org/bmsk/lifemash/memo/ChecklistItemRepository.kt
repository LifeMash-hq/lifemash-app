package org.bmsk.lifemash.memo

import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.SyncChecklistItemEntry
import kotlin.uuid.Uuid

interface ChecklistItemRepository {
    fun findByMemo(memoId: Uuid): List<ChecklistItemDto>
    fun sync(memoId: Uuid, items: List<SyncChecklistItemEntry>): List<ChecklistItemDto>
}
