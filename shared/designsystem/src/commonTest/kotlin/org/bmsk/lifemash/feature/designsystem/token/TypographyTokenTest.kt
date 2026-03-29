package org.bmsk.lifemash.feature.designsystem.token

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.bmsk.lifemash.feature.designsystem.theme.LifeMashTypography
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypographyTokenTest {

    @Test
    fun `Semibold weight가 존재한다`() {
        val semiboldStyles = listOf(
            LifeMashTypography.displaySmall,
            LifeMashTypography.headlineMedium,
            LifeMashTypography.headlineSmall,
            LifeMashTypography.titleMedium,
        )
        assertTrue(semiboldStyles.all { it.fontWeight == FontWeight.SemiBold })
    }

    @Test
    fun `BodyMedium은 14sp이다`() {
        assertEquals(14.sp, LifeMashTypography.bodyMedium.fontSize)
    }
}
