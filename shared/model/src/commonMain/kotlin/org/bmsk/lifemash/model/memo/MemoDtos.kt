package org.bmsk.lifemash.model.memo

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MemoDto(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val isChecklist: Boolean,
    val checklistItems: List<ChecklistItemDto> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Serializable
data class ChecklistItemDto(
    val id: String,
    val content: String,
    val isChecked: Boolean,
    val sortOrder: Int,
)

@Serializable
data class CreateMemoRequest(
    val title: String,
    val content: String = "",
    val isPinned: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: List<CreateChecklistItemRequest> = emptyList(),
)

@Serializable
data class CreateChecklistItemRequest(
    val content: String,
    val isChecked: Boolean = false,
    val sortOrder: Int = 0,
)

@Serializable
data class UpdateMemoRequest(
    val title: String? = null,
    val content: String? = null,
    val isPinned: Boolean? = null,
)

@Serializable
data class SyncChecklistRequest(
    val items: List<SyncChecklistItemEntry>,
)

@Serializable
data class SyncChecklistItemEntry(
    val id: String? = null,
    val content: String,
    val isChecked: Boolean = false,
    val sortOrder: Int = 0,
)
