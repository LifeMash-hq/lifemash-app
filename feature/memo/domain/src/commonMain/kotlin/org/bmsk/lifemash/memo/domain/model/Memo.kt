package org.bmsk.lifemash.memo.domain.model

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Memo(
    val id: String,
    val groupId: String,
    val authorId: String,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val isChecklist: Boolean,
    val checklistItems: List<ChecklistItem>,
    val createdAt: Instant,
    val updatedAt: Instant,
)
