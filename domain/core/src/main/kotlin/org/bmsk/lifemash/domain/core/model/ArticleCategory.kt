package org.bmsk.lifemash.domain.core.model

enum class ArticleCategory(val key: String) {
    ALL("_all_"),
    POLITICS("politics"),
    ECONOMY("economy"),
    SOCIETY("society"),
    INTERNATIONAL("international"),
    SPORTS("sports"),
    CULTURE("culture"),
    ENTERTAINMENT("entertainment"),
    TECH("tech"),
    SCIENCE("science"),
    COLUMN("column"),
    PEOPLE("people"),
    HEALTH("health"),
    MEDICAL("medical"),
    WOMEN("women"),
    CARTOON("cartoon");

    companion object {
        fun fromKey(key: String): ArticleCategory {
            return entries.first { it.key == key }
        }
    }
}
