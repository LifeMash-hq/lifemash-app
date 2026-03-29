package org.bmsk.lifemash.feature.designsystem.token

import androidx.compose.ui.graphics.Color
import org.bmsk.lifemash.feature.designsystem.theme.BgPage
import org.bmsk.lifemash.feature.designsystem.theme.DarkBg
import org.bmsk.lifemash.feature.designsystem.theme.DarkPrimary
import org.bmsk.lifemash.feature.designsystem.theme.DarkSurface
import org.bmsk.lifemash.feature.designsystem.theme.Danger
import org.bmsk.lifemash.feature.designsystem.theme.Primary
import org.bmsk.lifemash.feature.designsystem.theme.PrimaryDark
import org.bmsk.lifemash.feature.designsystem.theme.PrimaryLight
import org.bmsk.lifemash.feature.designsystem.theme.Success
import org.bmsk.lifemash.feature.designsystem.theme.Surface
import org.bmsk.lifemash.feature.designsystem.theme.Warning
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTokenTest {

    @Test
    fun `Primary는 6C5CE7이다`() {
        assertEquals(Color(0xFF6C5CE7), Primary)
    }

    @Test
    fun `PrimaryLight는 F3F1FF이다`() {
        assertEquals(Color(0xFFF3F1FF), PrimaryLight)
    }

    @Test
    fun `PrimaryDark는 5A4BD1이다`() {
        assertEquals(Color(0xFF5A4BD1), PrimaryDark)
    }

    @Test
    fun `Danger는 EF4444이다`() {
        assertEquals(Color(0xFFEF4444), Danger)
    }

    @Test
    fun `Success는 22C55E이다`() {
        assertEquals(Color(0xFF22C55E), Success)
    }

    @Test
    fun `Warning은 F59E0B이다`() {
        assertEquals(Color(0xFFF59E0B), Warning)
    }

    @Test
    fun `Background는 FAFAFA이다`() {
        assertEquals(Color(0xFFFAFAFA), BgPage)
    }

    @Test
    fun `Surface는 FFFFFF이다`() {
        assertEquals(Color(0xFFFFFFFF), Surface)
    }

    @Test
    fun `다크_Primary는 7B6CF0이다`() {
        assertEquals(Color(0xFF7B6CF0), DarkPrimary)
    }

    @Test
    fun `다크_Surface는 1E1E22이다`() {
        assertEquals(Color(0xFF1E1E22), DarkSurface)
    }

    @Test
    fun `다크_Background는 161618이다`() {
        assertEquals(Color(0xFF161618), DarkBg)
    }
}
