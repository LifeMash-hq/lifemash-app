package org.bmsk.lifemash.validation

import kotlin.jvm.JvmInline
import kotlin.random.Random

@JvmInline
value class InviteCode private constructor(val value: String) {
    companion object {
        const val LENGTH = 8
        private val PATTERN = Regex("^[A-Z0-9]{$LENGTH}$")

        fun of(code: String): InviteCode {
            require(PATTERN.matches(code)) { "올바르지 않은 초대 코드입니다" }
            return InviteCode(code)
        }

        fun generate(): InviteCode {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val code = (1..LENGTH).map { chars[Random.nextInt(chars.length)] }.joinToString("")
            return InviteCode(code)
        }
    }
}
