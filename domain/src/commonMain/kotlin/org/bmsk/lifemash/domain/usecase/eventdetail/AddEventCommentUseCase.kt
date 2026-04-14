package org.bmsk.lifemash.domain.usecase.eventdetail

import org.bmsk.lifemash.domain.eventdetail.EventComment
import org.bmsk.lifemash.domain.eventdetail.EventDetailRepository

class AddEventCommentUseCase(private val repository: EventDetailRepository) {
    suspend operator fun invoke(eventId: String, content: String): EventComment =
        repository.addComment(eventId, content)
}
