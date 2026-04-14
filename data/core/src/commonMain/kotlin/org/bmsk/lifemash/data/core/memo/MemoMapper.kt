package org.bmsk.lifemash.data.core.memo

import org.bmsk.lifemash.data.remote.memo.dto.ChecklistItemDto
import org.bmsk.lifemash.data.remote.memo.dto.MemoDto
import org.bmsk.lifemash.domain.memo.ChecklistItem
import org.bmsk.lifemash.domain.memo.Memo

internal fun MemoDto.toDomain(): Memo =
    Memo(
        id = id,
        groupId = groupId,
        authorId = authorId,
        title = title,
        content = content,
        isPinned = isPinned,
        isChecklist = isChecklist,
        checklistItems = checklistItems.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

internal fun ChecklistItemDto.toDomain(): ChecklistItem =
    ChecklistItem(
        id = id,
        content = content,
        isChecked = isChecked,
        sortOrder = sortOrder,
    )
