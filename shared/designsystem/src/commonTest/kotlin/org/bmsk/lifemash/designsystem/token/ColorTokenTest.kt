package org.bmsk.lifemash.designsystem.token

import androidx.compose.ui.graphics.Color
import org.bmsk.lifemash.designsystem.theme.BgPage
import org.bmsk.lifemash.designsystem.theme.DarkBg
import org.bmsk.lifemash.designsystem.theme.DarkOverlay
import org.bmsk.lifemash.designsystem.theme.DarkPrimary
import org.bmsk.lifemash.designsystem.theme.DarkSurface
import org.bmsk.lifemash.designsystem.theme.DarkTextDisabled
import org.bmsk.lifemash.designsystem.theme.Danger
import org.bmsk.lifemash.designsystem.theme.OnDanger
import org.bmsk.lifemash.designsystem.theme.OnSuccess
import org.bmsk.lifemash.designsystem.theme.Overlay
import org.bmsk.lifemash.designsystem.theme.Primary
import org.bmsk.lifemash.designsystem.theme.PrimaryDark
import org.bmsk.lifemash.designsystem.theme.PrimaryLight
import org.bmsk.lifemash.designsystem.theme.Success
import org.bmsk.lifemash.designsystem.theme.Surface
import org.bmsk.lifemash.designsystem.theme.TextDisabled
import org.bmsk.lifemash.designsystem.theme.Warning
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

    // ── 신규 토큰 ──────────────────────────────────────────────────────────

    @Test
    fun `OnDanger는 FFFFFF이다`() {
        assertEquals(Color(0xFFFFFFFF), OnDanger)
    }

    @Test
    fun `OnSuccess는 FFFFFF이다`() {
        assertEquals(Color(0xFFFFFFFF), OnSuccess)
    }

    @Test
    fun `Overlay는 30퍼센트 검정이다`() {
        assertEquals(Color(0x4D000000), Overlay)
    }

    @Test
    fun `다크_Overlay는 60퍼센트 검정이다`() {
        assertEquals(Color(0x99000000), DarkOverlay)
    }

    @Test
    fun `TextDisabled는 BBBBBB이다`() {
        assertEquals(Color(0xFFBBBBBB), TextDisabled)
    }

    @Test
    fun `다크_TextDisabled는 444444이다`() {
        assertEquals(Color(0xFF444444), DarkTextDisabled)
    }
}
