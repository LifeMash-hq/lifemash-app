package org.bmsk.lifemash.domain.usecase.calendar

import org.bmsk.lifemash.domain.calendar.Group
import org.bmsk.lifemash.domain.calendar.GroupRepository

class JoinGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(inviteCode: String): Group = repository.joinGroup(inviteCode)
}
