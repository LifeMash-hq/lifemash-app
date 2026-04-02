package org.bmsk.lifemash.memo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChecklistItem(
    val id: String,
    val content: String,
    val isChecked: Boolean,
    val sortOrder: Int,
)
