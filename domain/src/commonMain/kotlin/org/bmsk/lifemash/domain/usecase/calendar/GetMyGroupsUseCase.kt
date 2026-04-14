package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupRepository

class GetMyGroupsUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(): List<Group> = repository.getMyGroups()
}
