package org.bmsk.lifemash.designsystem.token

import androidx.compose.ui.unit.dp
import org.bmsk.lifemash.designsystem.theme.LifeMashSpacing
import kotlin.test.Test
import kotlin.test.assertEquals

class SpacingTokenTest {

    @Test
    fun `XXSmall은 2dp이다`() {
        assertEquals(2.dp, LifeMashSpacing.micro)
    }

    @Test
    fun `Huge는 40dp이다`() {
        assertEquals(40.dp, LifeMashSpacing.huge)
    }
}
