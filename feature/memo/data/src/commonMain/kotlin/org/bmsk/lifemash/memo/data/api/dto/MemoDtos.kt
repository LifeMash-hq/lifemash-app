package org.bmsk.lifemash.memo.data.api.dto

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import org.bmsk.lifemash.memo.domain.model.ChecklistItem
import org.bmsk.lifemash.memo.domain.model.Memo

@Serializable
data class MemoDto(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val isChecklist: Boolean,
    val checklistItems: List<ChecklistItemDto>,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun toDomain() = Memo(
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
}

@Serializable
data class ChecklistItemDto(
    val id: String,
    val content: String,
    val isChecked: Boolean,
    val sortOrder: Int,
) {
    fun toDomain() = ChecklistItem(
        id = id,
        content = content,
        isChecked = isChecked,
        sortOrder = sortOrder,
    )
}

@Serializable
data class CreateMemoBody(
    val title: String,
    val content: String = "",
    val isPinned: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: List<SyncChecklistItemEntry> = emptyList(),
)

@Serializable
data class UpdateMemoBody(
    val title: String?,
    val content: String?,
    val isPinned: Boolean?,
)

@Serializable
data class SyncChecklistBody(
    val items: List<SyncChecklistItemEntry>,
)

@Serializable
data class SyncChecklistItemEntry(
    val id: String? = null,
    val content: String,
    val isChecked: Boolean = false,
    val sortOrder: Int = 0,
)
