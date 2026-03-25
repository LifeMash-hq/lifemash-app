package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventTitleTest {

    @Test
    fun `blank title throws`() {
        assertFailsWith<IllegalArgumentException> { EventTitle.of("   ") }
    }

    @Test
    fun `empty title throws`() {
        assertFailsWith<IllegalArgumentException> { EventTitle.of("") }
    }

    @Test
    fun `too long title throws`() {
        val longTitle = "a".repeat(201)
        assertFailsWith<IllegalArgumentException> { EventTitle.of(longTitle) }
    }

    @Test
    fun `valid title succeeds`() {
        val title = EventTitle.of("팀 미팅")
        assertEquals("팀 미팅", title.value)
    }

    @Test
    fun `title is trimmed`() {
        val title = EventTitle.of("  팀 미팅  ")
        assertEquals("팀 미팅", title.value)
    }
}
