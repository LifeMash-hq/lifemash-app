package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GroupNameTest {

    @Test
    fun `blank name throws`() {
        assertFailsWith<IllegalArgumentException> { GroupName.of("   ") }
    }

    @Test
    fun `empty name throws`() {
        assertFailsWith<IllegalArgumentException> { GroupName.of("") }
    }

    @Test
    fun `too long name throws`() {
        val longName = "a".repeat(21)
        assertFailsWith<IllegalArgumentException> { GroupName.of(longName) }
    }

    @Test
    fun `valid name succeeds`() {
        val name = GroupName.of("우리 가족")
        assertEquals("우리 가족", name.value)
    }

    @Test
    fun `name is trimmed`() {
        val name = GroupName.of("  우리 가족  ")
        assertEquals("우리 가족", name.value)
    }

    @Test
    fun `boundary - exactly MAX_LENGTH chars succeeds`() {
        val exactly20 = "a".repeat(20)
        val name = GroupName.of(exactly20)
        assertEquals(exactly20, name.value)
    }
}
