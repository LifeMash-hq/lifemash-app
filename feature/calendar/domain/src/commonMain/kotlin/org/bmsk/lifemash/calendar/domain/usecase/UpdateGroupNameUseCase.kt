package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

interface UpdateGroupNameUseCase {
    suspend operator fun invoke(groupId: String, name: String): Group
}

class UpdateGroupNameUseCaseImpl(
    private val repository: GroupRepository,
) : UpdateGroupNameUseCase {
    override suspend operator fun invoke(groupId: String, name: String): Group =
        repository.updateGroupName(groupId, name)
}
