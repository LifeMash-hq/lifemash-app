package org.bmsk.lifemash.core.network.response

data class LifeMashArticleResponse(
    val id: String = "",
    val publisher: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val link: String? = null,
    val image: String? = null,
    val publishedAt: Long? = null,
    val host: String? = null,
    // 파이어베이스로부터 가져올 때 매핑하기 위해선 List<String>이어야 함
    val categories: List<LifeMashArticleCategory> = emptyList(),
    val visible: Boolean = true
)

enum class LifeMashArticleCategory(val key: String) {
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

    companion object Companion {
        // 만일 key가 올바르지 않으면 예외를 던지는 것을 허용함
        fun fromKey(key: String): LifeMashArticleCategory {
            return entries.first { it.key == key }
        }
    }
}