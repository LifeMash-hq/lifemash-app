package org.bmsk.lifemash.domain.usecase.profile

import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.ProfileSettingsRepository

class UpdateProfileSettingsUseCase(private val repository: ProfileSettingsRepository) {
    suspend operator fun invoke(settings: ProfileSettings) {
        repository.update(settings)
    }
}
