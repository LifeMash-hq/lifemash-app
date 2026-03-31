package org.bmsk.lifemash.designsystem.token

import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashRadius
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapeTokenTest {

    @Test
    fun `Small은 6dp이다`() {
        assertEquals(6.dp, LifeMashRadius.sm)
    }

    @Test
    fun `Medium은 10dp이다`() {
        assertEquals(10.dp, LifeMashRadius.md)
    }

    @Test
    fun `Large는 16dp이다`() {
        assertEquals(16.dp, LifeMashRadius.lg)
    }

    @Test
    fun `ExtraLarge는 20dp이다`() {
        assertEquals(20.dp, LifeMashRadius.xl)
    }
}
