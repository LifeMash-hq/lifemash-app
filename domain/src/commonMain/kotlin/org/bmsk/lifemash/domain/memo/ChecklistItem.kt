package org.bmsk.lifemash.domain.memo

data class ChecklistItem(
    val id: String,
    val content: String,
    val isChecked: Boolean,
    val sortOrder: Int,
)
