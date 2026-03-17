package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.model.GroupType
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

interface CreateGroupUseCase {
    suspend operator fun invoke(type: GroupType = GroupType.COUPLE, name: String? = null): Group
}

class CreateGroupUseCaseImpl(
    private val repository: GroupRepository,
) : CreateGroupUseCase {
    override suspend operator fun invoke(type: GroupType, name: String?): Group =
        repository.createGroup(type, name)
}
