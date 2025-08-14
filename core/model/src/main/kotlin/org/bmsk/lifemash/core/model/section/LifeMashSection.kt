package org.bmsk.lifemash.core.model.section

enum class LifeMashSection(val id: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    companion object {
        fun fromId(id: String): LifeMashSection = entries.first { entry ->
            entry.id.equals(id, ignoreCase = true)
        }
    }
}