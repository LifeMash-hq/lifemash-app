package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InviteCodeTest {

    @Test
    fun `invalid format throws`() {
        assertFailsWith<IllegalArgumentException> { InviteCode.of("ABC") }
    }

    @Test
    fun `lowercase throws`() {
        assertFailsWith<IllegalArgumentException> { InviteCode.of("abcd1234") }
    }

    @Test
    fun `special characters throw`() {
        assertFailsWith<IllegalArgumentException> { InviteCode.of("ABCD-234") }
    }

    @Test
    fun `valid code succeeds`() {
        val code = InviteCode.of("ABCD1234")
        assertEquals("ABCD1234", code.value)
    }

    @Test
    fun `generate produces valid format`() {
        repeat(10) {
            val code = InviteCode.generate()
            assertEquals(InviteCode.LENGTH, code.value.length)
            assertTrue(code.value.all { it in 'A'..'Z' || it in '0'..'9' })
        }
    }
}
