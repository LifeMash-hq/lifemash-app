package org.bmsk.lifemash.domain.profile

interface ProfileSettingsRepository {
    suspend fun get(): ProfileSettings
    suspend fun update(settings: ProfileSettings)
}
