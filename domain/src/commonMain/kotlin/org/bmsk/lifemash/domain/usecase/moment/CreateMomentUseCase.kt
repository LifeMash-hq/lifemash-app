package org.bmsk.lifemash.domain.usecase.moment

import org.bmsk.lifemash.domain.moment.MomentRepository
import org.bmsk.lifemash.domain.moment.Moment
import org.bmsk.lifemash.domain.moment.MomentMedia
import org.bmsk.lifemash.domain.moment.Visibility

class CreateMomentUseCase(private val repository: MomentRepository) {
    suspend operator fun invoke(
        eventId: String?,
        caption: String?,
        visibility: Visibility,
        media: List<MomentMedia>,
    ): Moment = repository.create(
        eventId = eventId,
        caption = caption,
        visibility = visibility,
        media = media,
    )
}
