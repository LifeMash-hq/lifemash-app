package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.domain.calendar.GroupType

class CreateGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(type: GroupType, name: String?): Group =
        repository.createGroup(type, name)
}
