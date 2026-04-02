package org.bmsk.lifemash.moment

import org.bmsk.lifemash.model.moment.CreateMomentRequest
import org.bmsk.lifemash.model.moment.MomentDto
import kotlin.uuid.Uuid

interface MomentRepository {
    fun create(authorId: Uuid, request: CreateMomentRequest): MomentDto
    fun findById(momentId: Uuid): MomentDto?
    fun findByUser(userId: Uuid, viewerId: Uuid?): List<MomentDto>
    fun delete(momentId: Uuid)
}
