package org.bmsk.lifemash.data.core.profile

import org.bmsk.lifemash.data.local.profile.ProfilePreferences
import org.bmsk.lifemash.domain.profile.CalendarViewMode
import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.ProfileSettingsRepository
import org.bmsk.lifemash.domain.profile.ProfileSubTab

internal class ProfileSettingsRepositoryImpl(
    private val prefs: ProfilePreferences,
) : ProfileSettingsRepository {

    override suspend fun get(): ProfileSettings = ProfileSettings(
        defaultSubTab = prefs.getDefaultSubTab().toProfileSubTab(),
        myCalendarViewMode = prefs.getMyCalendarViewMode().toCalendarViewMode(),
        othersCalendarViewMode = prefs.getOthersCalendarViewMode().toCalendarViewMode(),
        defaultEventVisibility = prefs.getDefaultEventVisibility(),
    )

    override suspend fun update(settings: ProfileSettings) {
        prefs.saveDefaultSubTab(settings.defaultSubTab.name)
        prefs.saveMyCalendarViewMode(settings.myCalendarViewMode.name)
        // TODO: API가 생기면 아래 2개는 원격 저장으로 전환
        prefs.saveOthersCalendarViewMode(settings.othersCalendarViewMode.name)
        prefs.saveDefaultEventVisibility(settings.defaultEventVisibility)
    }
}

private fun String.toProfileSubTab(): ProfileSubTab =
    runCatching { ProfileSubTab.valueOf(uppercase()) }.getOrDefault(ProfileSubTab.MOMENTS)

private fun String.toCalendarViewMode(): CalendarViewMode =
    runCatching { CalendarViewMode.valueOf(uppercase()) }.getOrDefault(CalendarViewMode.DOT)
