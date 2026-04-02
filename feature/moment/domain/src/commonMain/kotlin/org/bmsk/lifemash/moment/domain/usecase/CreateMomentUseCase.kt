package org.bmsk.lifemash.moment.domain.usecase

import org.bmsk.lifemash.moment.domain.model.Moment
import org.bmsk.lifemash.moment.domain.model.MomentMedia
import org.bmsk.lifemash.moment.domain.model.Visibility
import org.bmsk.lifemash.moment.domain.repository.MomentRepository

class CreateMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(
        eventId: String?,
        caption: String?,
        visibility: Visibility,
        media: List<MomentMedia>,
    ): Moment = repository.create(eventId, caption, visibility, media)
}
