package org.bmsk.lifemash.profile.impl

import org.bmsk.lifemash.domain.profile.CalendarViewMode
import org.bmsk.lifemash.domain.profile.ProfileEvent
import org.bmsk.lifemash.domain.profile.ProfileSubTab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileUiStateTest {

    private val sampleEvents = listOf(
        ProfileEvent(id = "e1", title = "스탠드업", startTime = "10:00", endTime = "10:15", color = "#4F6AF5"),
        ProfileEvent(id = "e2", title = "코드리뷰", startTime = "14:00", endTime = "15:00", color = "#F5A623"),
    )

    // ─── todayEvents 파생 속성 ─────────────────────────────────────────────────

    @Test
    fun `todayDay에 해당하는 dayEvents가 todayEvents로 반환된다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(15 to sampleEvents),
            todayDay = 15,
        )

        assertEquals(sampleEvents, state.todayEvents)
    }

    @Test
    fun `todayDay에 해당하는 dayEvents가 없으면 todayEvents는 빈 리스트다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(15 to sampleEvents),
            todayDay = 20,
        )

        assertTrue(state.todayEvents.isEmpty())
    }

    @Test
    fun `dayEvents가 비어있으면 todayEvents도 빈 리스트다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = emptyMap(),
            todayDay = 15,
        )

        assertTrue(state.todayEvents.isEmpty())
    }

    @Test
    fun `copy 후 todayDay가 바뀌면 todayEvents도 새로 계산된다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(
                10 to listOf(sampleEvents[0]),
                20 to listOf(sampleEvents[1]),
            ),
            todayDay = 10,
        )

        assertEquals(listOf(sampleEvents[0]), state.todayEvents)

        val updated = state.copy(todayDay = 20)
        assertEquals(listOf(sampleEvents[1]), updated.todayEvents)
    }

    // ─── selectedDayEvents 파생 속성 ───────────────────────────────────────────

    @Test
    fun `날짜 선택 시 해당 날짜의 이벤트가 selectedDayEvents로 반환된다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(15 to sampleEvents),
            selectedCalendarDay = 15,
        )

        assertEquals(sampleEvents, state.selectedDayEvents)
    }

    @Test
    fun `날짜 미선택 시 todayEvents가 selectedDayEvents로 반환된다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(10 to sampleEvents),
            todayDay = 10,
            selectedCalendarDay = null,
        )

        assertEquals(sampleEvents, state.selectedDayEvents)
    }

    @Test
    fun `선택한 날짜에 이벤트가 없으면 selectedDayEvents는 빈 리스트다`() {
        val state = ProfileUiState.Default.copy(
            dayEvents = mapOf(15 to sampleEvents),
            selectedCalendarDay = 20,
        )

        assertTrue(state.selectedDayEvents.isEmpty())
    }

    // ─── selectedDayLabel 파생 속성 ────────────────────────────────────────────

    @Test
    fun `날짜 선택 시 selectedDayLabel은 월일 형식이다`() {
        val state = ProfileUiState.Default.copy(
            selectedMonth = 3,
            selectedCalendarDay = 15,
        )

        assertEquals("3월 15일", state.selectedDayLabel)
    }

    @Test
    fun `날짜 미선택 시 selectedDayLabel은 오늘이다`() {
        val state = ProfileUiState.Default.copy(
            selectedCalendarDay = null,
        )

        assertEquals("오늘", state.selectedDayLabel)
    }

    // ─── withMonthDelta 변환 함수 ─────────────────────────────────────────────

    @Test
    fun `withMonthDelta +1은 월을 1 증가시킨다`() {
        val state = ProfileUiState.Default.copy(selectedYear = 2024, selectedMonth = 3)

        val result = state.withMonthDelta(1)

        assertEquals(2024, result.selectedYear)
        assertEquals(4, result.selectedMonth)
    }

    @Test
    fun `12월에서 withMonthDelta +1은 다음 해 1월이 된다`() {
        val state = ProfileUiState.Default.copy(selectedYear = 2024, selectedMonth = 12)

        val result = state.withMonthDelta(1)

        assertEquals(2025, result.selectedYear)
        assertEquals(1, result.selectedMonth)
    }

    @Test
    fun `1월에서 withMonthDelta -1은 전년 12월이 된다`() {
        val state = ProfileUiState.Default.copy(selectedYear = 2024, selectedMonth = 1)

        val result = state.withMonthDelta(-1)

        assertEquals(2023, result.selectedYear)
        assertEquals(12, result.selectedMonth)
    }

    @Test
    fun `withMonthDelta는 selectedCalendarDay를 null로 초기화한다`() {
        val state = ProfileUiState.Default.copy(
            selectedYear = 2024,
            selectedMonth = 3,
            selectedCalendarDay = 15,
        )

        val result = state.withMonthDelta(1)

        assertNull(result.selectedCalendarDay)
    }

    // ─── isReady 파생 속성 ────────────────────────────────────────────────────

    @Test
    fun `screenPhase가 Ready이면 isReady는 true다`() {
        val state = ProfileUiState.Default.copy(screenPhase = ScreenPhase.Ready)

        assertTrue(state.isReady)
    }

    @Test
    fun `screenPhase가 Initializing이면 isReady는 false다`() {
        assertFalse(ProfileUiState.Default.isReady)
    }

    @Test
    fun `screenPhase가 FatalError이면 isReady는 false다`() {
        val state = ProfileUiState.Default.copy(
            screenPhase = ScreenPhase.FatalError("오류"),
        )

        assertFalse(state.isReady)
    }

    // ─── Default 초기값 ──────────────────────────────────────────────────────

    @Test
    fun `Default는 Initializing 상태이고 profile과 groupId는 null이다`() {
        val default = ProfileUiState.Default

        assertEquals(ScreenPhase.Initializing, default.screenPhase)
        assertNull(default.profile)
        assertNull(default.groupId)
        assertEquals(ProfileSubTab.MOMENTS, default.selectedSubTab)
        assertEquals(CalendarViewMode.DOT, default.calendarViewMode)
        assertFalse(default.isFollowInProgress)
        assertFalse(default.isFollowSheetVisible)
    }
}
