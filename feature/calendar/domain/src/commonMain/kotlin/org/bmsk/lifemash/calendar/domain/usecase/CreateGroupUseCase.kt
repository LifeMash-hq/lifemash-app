package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

class CreateGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(type: GroupType = GroupType.COUPLE, name: String? = null): Group =
        repository.createGroup(type, name)
}
