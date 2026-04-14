package org.bmsk.lifemash.domain.profile

enum class ProfileSubTab { MOMENTS, CALENDAR }

enum class CalendarViewMode { DOT, CHIP }

data class ProfileSettings(
    val defaultSubTab: ProfileSubTab,
    val myCalendarViewMode: CalendarViewMode,
    val othersCalendarViewMode: CalendarViewMode,
    val defaultEventVisibility: String,
) {
    companion object {
        val Default = ProfileSettings(
            defaultSubTab = ProfileSubTab.MOMENTS,
            myCalendarViewMode = CalendarViewMode.DOT,
            othersCalendarViewMode = CalendarViewMode.DOT,
            defaultEventVisibility = "public",
        )
    }
}
