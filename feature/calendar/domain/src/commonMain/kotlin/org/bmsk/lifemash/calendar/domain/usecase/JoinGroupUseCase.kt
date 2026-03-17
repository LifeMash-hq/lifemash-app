package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

interface JoinGroupUseCase {
    suspend operator fun invoke(inviteCode: String): Group
}

class JoinGroupUseCaseImpl(
    private val repository: GroupRepository,
) : JoinGroupUseCase {
    override suspend operator fun invoke(inviteCode: String): Group =
        repository.joinGroup(inviteCode)
}
