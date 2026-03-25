package org.bmsk.lifemash.validation

import kotlin.jvm.JvmInline

@JvmInline
value class ChatMessageContent private constructor(val value: String) {
    companion object {
        const val MAX_LENGTH = 2000

        fun of(message: String): ChatMessageContent {
            require(message.isNotBlank()) { "메시지를 입력해주세요" }
            require(message.length <= MAX_LENGTH) { "메시지는 ${MAX_LENGTH}자 이하여야 합니다" }
            return ChatMessageContent(message)
        }
    }
}
