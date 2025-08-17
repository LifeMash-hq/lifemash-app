package org.bmsk.lifemash.core.model.section

enum class LifeMashCategory(val id: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    companion object Companion {
        fun fromId(id: String): LifeMashCategory = entries.first { entry ->
            entry.id.equals(id, ignoreCase = true)
        }
    }
}