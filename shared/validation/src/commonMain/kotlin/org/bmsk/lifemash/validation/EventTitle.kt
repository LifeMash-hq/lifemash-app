package org.bmsk.lifemash.validation

import kotlin.jvm.JvmInline

@JvmInline
value class EventTitle private constructor(val value: String) {
    companion object {
        const val MAX_LENGTH = 200

        fun of(title: String): EventTitle {
            require(title.isNotBlank()) { "일정 제목을 입력해주세요" }
            val trimmed = title.trim()
            require(trimmed.length <= MAX_LENGTH) { "일정 제목은 ${MAX_LENGTH}자 이하여야 합니다" }
            return EventTitle(trimmed)
        }
    }
}
