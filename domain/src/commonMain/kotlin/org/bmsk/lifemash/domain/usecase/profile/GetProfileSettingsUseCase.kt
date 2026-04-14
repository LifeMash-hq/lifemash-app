package org.bmsk.lifemash.domain.usecase.profile

import org.bmsk.lifemash.domain.profile.ProfileRepository
import org.bmsk.lifemash.domain.profile.ProfileSettings

class GetProfileSettingsUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): ProfileSettings = repository.getProfileSettings()
}
