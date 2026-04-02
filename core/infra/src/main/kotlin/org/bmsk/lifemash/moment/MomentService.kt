package org.bmsk.lifemash.moment

import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MomentDto
import org.bmsk.lifemash.plugins.BadRequestException
import org.bmsk.lifemash.plugins.ForbiddenException
import org.bmsk.lifemash.plugins.NotFoundException
import kotlin.uuid.Uuid

class MomentService(
    private val momentRepository: MomentRepository,
) {
    fun create(authorId: Uuid, request: CreateMomentRequest): MomentDto {
        if (request.caption.isNullOrBlank() && request.media.isEmpty()) {
            throw BadRequestException("텍스트 또는 미디어가 하나 이상 필요합니다")
        }
        if (request.media.size > 10) {
            throw BadRequestException("미디어는 최대 10개까지 첨부할 수 있습니다")
        }
        if (request.visibility !in VALID_VISIBILITY) {
            throw BadRequestException("공개범위는 public, followers, private 중 하나여야 합니다")
        }
        return momentRepository.create(authorId, request)
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

    companion object {
        private val VALID_VISIBILITY = setOf("public", "followers", "private")
    }
}
