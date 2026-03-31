package org.bmsk.lifemash.validation

import kotlin.jvm.JvmInline

@JvmInline
value class CommentContent private constructor(val value: String) {
    companion object {
        fun of(content: String): CommentContent {
            require(content.isNotBlank()) { "댓글을 입력해주세요" }
            return CommentContent(content)
        }
    }
}
