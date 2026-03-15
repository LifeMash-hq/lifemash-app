package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

class GetMyGroupsUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(): List<Group> = repository.getMyGroups()
}
