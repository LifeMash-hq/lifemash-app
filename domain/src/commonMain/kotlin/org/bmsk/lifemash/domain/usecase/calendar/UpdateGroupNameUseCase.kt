package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupRepository

class UpdateGroupNameUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(groupId: String, name: String): Group =
        repository.updateGroupName(groupId, name)
}
