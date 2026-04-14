package org.bmsk.lifemash.domain.usecase.profile

import org.bmsk.lifemash.domain.profile.ProfileSettings
import org.bmsk.lifemash.domain.profile.ProfileSettingsRepository

class GetProfileSettingsUseCase(private val repository: ProfileSettingsRepository) {
    suspend operator fun invoke(): ProfileSettings = repository.get()
}
