package org.bmsk.lifemash.notification.domain.model

import kotlin.time.Instant
import kotlin.jvm.JvmInline

data class NotificationKeyword(
    val id: Long = 0,
    val keyword: Keyword,
    val createdAt: Instant,
)

@JvmInline
value class Keyword(val value: String) {
    init {
        require(value.isNotBlank()) { "키워드는 빈 문자열일 수 없습니다" }
    }

    companion object {
        fun from(raw: String): Keyword = Keyword(raw.trim().lowercase())
    }
}
