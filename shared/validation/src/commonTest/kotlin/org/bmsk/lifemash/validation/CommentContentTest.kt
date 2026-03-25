package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommentContentTest {

    @Test
    fun `blank content throws`() {
        assertFailsWith<IllegalArgumentException> { CommentContent.of("   ") }
    }

    @Test
    fun `empty content throws`() {
        assertFailsWith<IllegalArgumentException> { CommentContent.of("") }
    }

    @Test
    fun `valid content succeeds`() {
        val content = CommentContent.of("좋아요!")
        assertEquals("좋아요!", content.value)
    }
}
