package org.bmsk.lifemash.moment

import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

class MomentService(
    private val momentRepository: MomentRepository,
) {
    fun create(eventId: Uuid, authorId: Uuid, request: CreateMomentRequest): MomentDto {
        return momentRepository.create(eventId, authorId, request.imageUrl, request.caption, request.visibility)
    }

    fun findByUser(userId: Uuid, viewerId: Uuid?): List<MomentDto> {
        return momentRepository.findByUser(userId, viewerId)
    }

    fun delete(momentId: Uuid, requesterId: Uuid) {
        val moment = momentRepository.findById(momentId)
            ?: throw NotFoundException("순간을 찾을 수 없습니다")
        if (moment.authorId != requesterId.toString()) {
            throw ForbiddenException("본인의 순간만 삭제할 수 있습니다")
        }
        momentRepository.delete(momentId)
    }
}
