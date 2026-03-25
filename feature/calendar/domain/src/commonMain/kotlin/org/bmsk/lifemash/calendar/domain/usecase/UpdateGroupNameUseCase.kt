package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.validation.GroupName

interface UpdateGroupNameUseCase {
    suspend operator fun invoke(groupId: String, name: String): Group
}

class UpdateGroupNameUseCaseImpl(
    private val repository: GroupRepository,
) : UpdateGroupNameUseCase {
    override suspend operator fun invoke(groupId: String, name: String): Group {
        val validatedName = GroupName.of(name)
        return repository.updateGroupName(groupId, validatedName.value)
    }
}
