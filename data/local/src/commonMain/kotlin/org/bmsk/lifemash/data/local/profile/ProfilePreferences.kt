package org.bmsk.lifemash.data.local.profile

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class ProfilePreferences(private val dataStore: DataStore<Preferences>) {

    suspend fun getDefaultSubTab(): String =
        dataStore.data.first()[DEFAULT_SUB_TAB_KEY] ?: "moments"

    suspend fun getMyCalendarViewMode(): String =
        dataStore.data.first()[MY_CALENDAR_VIEW_KEY] ?: "dot"

    suspend fun getOthersCalendarViewMode(): String =
        dataStore.data.first()[OTHERS_CALENDAR_VIEW_KEY] ?: "dot"

    suspend fun getDefaultEventVisibility(): String =
        dataStore.data.first()[DEFAULT_VISIBILITY_KEY] ?: "public"

    suspend fun saveDefaultSubTab(value: String) {
        dataStore.edit { it[DEFAULT_SUB_TAB_KEY] = value }
    }

    suspend fun saveMyCalendarViewMode(value: String) {
        dataStore.edit { it[MY_CALENDAR_VIEW_KEY] = value }
    }

    suspend fun saveOthersCalendarViewMode(value: String) {
        dataStore.edit { it[OTHERS_CALENDAR_VIEW_KEY] = value }
    }

    suspend fun saveDefaultEventVisibility(value: String) {
        dataStore.edit { it[DEFAULT_VISIBILITY_KEY] = value }
    }

    private companion object {
        val DEFAULT_SUB_TAB_KEY = stringPreferencesKey("profile_default_sub_tab")
        val MY_CALENDAR_VIEW_KEY = stringPreferencesKey("profile_my_calendar_view")
        val OTHERS_CALENDAR_VIEW_KEY = stringPreferencesKey("profile_others_calendar_view")
        val DEFAULT_VISIBILITY_KEY = stringPreferencesKey("profile_default_visibility")
    }
}
