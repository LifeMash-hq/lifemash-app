package org.bmsk.lifemash.group

@JvmInline
value class GroupName private constructor(private val value: String) {

    /** 저장/전달용 원시 문자열 반환 */
    fun asString(): String = value

    companion object {
        fun of(raw: String): GroupName {
            val trimmed = raw.trim()
            require(trimmed.isNotBlank()) { "그룹명은 공백일 수 없습니다" }
            require(trimmed.length <= 20) { "그룹명은 20자 이하여야 합니다" }
            return GroupName(trimmed)
        }
    }
}
