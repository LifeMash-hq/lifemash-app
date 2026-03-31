package org.bmsk.lifemash.designsystem.token

import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashShadow
import kotlin.test.Test
import kotlin.test.assertEquals

class ShadowTokenTest {

    @Test
    fun `shadow sm은 2dp이다`() {
        assertEquals(2.dp, LifeMashShadow.sm)
    }

    @Test
    fun `shadow md는 6dp이다`() {
        assertEquals(6.dp, LifeMashShadow.md)
    }

    @Test
    fun `shadow lg는 16dp이다`() {
        assertEquals(16.dp, LifeMashShadow.lg)
    }
}
