package org.bmsk.lifemash.calendar.domain.usecase

import org.bmsk.lifemash.calendar.domain.model.Group
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository

class JoinGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(inviteCode: String): Group =
        repository.joinGroup(inviteCode)
}
