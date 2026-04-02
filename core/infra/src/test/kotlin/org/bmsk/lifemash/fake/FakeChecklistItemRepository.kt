package org.bmsk.lifemash.fake

import org.bmsk.lifemash.memo.ChecklistItemRepository
import org.bmsk.lifemash.model.memo.ChecklistItemDto
import org.bmsk.lifemash.model.memo.SyncChecklistItemEntry
import kotlin.uuid.Uuid

class FakeChecklistItemRepository : ChecklistItemRepository {
    private val store = mutableListOf<ChecklistItemDto>()

    override fun findByMemo(memoId: Uuid): List<ChecklistItemDto> =
        store.sortedBy { it.sortOrder }

    override fun sync(memoId: Uuid, items: List<SyncChecklistItemEntry>): List<ChecklistItemDto> {
        store.clear()
        items.forEach { entry ->
            store.add(
                ChecklistItemDto(
                    id = entry.id ?: Uuid.random().toString(),
                    content = entry.content,
                    isChecked = entry.isChecked,
                    sortOrder = entry.sortOrder,
                )
            )
        }
        return store.sortedBy { it.sortOrder }
    }
}
