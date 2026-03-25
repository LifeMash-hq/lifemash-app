package org.bmsk.lifemash.validation

import kotlin.jvm.JvmInline

@JvmInline
value class GroupName private constructor(val value: String) {
    companion object {
        const val MAX_LENGTH = 20

        fun of(name: String): GroupName {
            require(name.isNotBlank()) { "그룹 이름을 입력해주세요" }
            val trimmed = name.trim()
            require(trimmed.length <= MAX_LENGTH) { "그룹 이름은 ${MAX_LENGTH}자 이하여야 합니다" }
            return GroupName(trimmed)
        }
    }
}
