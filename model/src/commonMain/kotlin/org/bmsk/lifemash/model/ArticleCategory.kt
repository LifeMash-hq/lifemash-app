package org.bmsk.lifemash.model

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
    CARTOON("cartoon"),
    UNKNOWN("unknown");

    companion object {
        fun fromKey(key: String): ArticleCategory =
            entries.firstOrNull { it.key == key } ?: UNKNOWN
    }
}
