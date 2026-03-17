package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

interface GetMyGroupsUseCase {
    suspend operator fun invoke(): List<Group>
}

class GetMyGroupsUseCaseImpl(
    private val repository: GroupRepository,
) : GetMyGroupsUseCase {
    override suspend operator fun invoke(): List<Group> = repository.getMyGroups()
}
