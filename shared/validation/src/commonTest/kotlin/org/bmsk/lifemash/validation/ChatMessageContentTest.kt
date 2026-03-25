package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChatMessageContentTest {

    @Test
    fun `blank message throws`() {
        assertFailsWith<IllegalArgumentException> { ChatMessageContent.of("   ") }
    }

    @Test
    fun `empty message throws`() {
        assertFailsWith<IllegalArgumentException> { ChatMessageContent.of("") }
    }

    @Test
    fun `too long message throws`() {
        val longMessage = "a".repeat(2001)
        assertFailsWith<IllegalArgumentException> { ChatMessageContent.of(longMessage) }
    }

    @Test
    fun `valid message succeeds`() {
        val content = ChatMessageContent.of("안녕하세요")
        assertEquals("안녕하세요", content.value)
    }

    @Test
    fun `boundary - exactly MAX_LENGTH chars succeeds`() {
        val exactly2000 = "a".repeat(2000)
        val content = ChatMessageContent.of(exactly2000)
        assertEquals(exactly2000, content.value)
    }
}
