package org.bmsk.lifemash.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class GroupLimitsTest {

    @Test
    fun `couple group has max 2 members`() {
        assertEquals(2, GroupLimits.maxMembers("COUPLE"))
    }

    @Test
    fun `family group has max 50 members`() {
        assertEquals(50, GroupLimits.maxMembers("FAMILY"))
    }

    @Test
    fun `other group types have max 50 members`() {
        assertEquals(50, GroupLimits.maxMembers("FRIENDS"))
        assertEquals(50, GroupLimits.maxMembers("WORK"))
    }
}
