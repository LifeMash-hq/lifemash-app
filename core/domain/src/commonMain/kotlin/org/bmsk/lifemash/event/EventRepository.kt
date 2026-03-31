package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import kotlin.uuid.Uuid

interface EventRepository {
    fun getMonthEvents(groupId: Uuid, year: Int, month: Int): List<EventDto>
    fun create(groupId: Uuid, authorId: Uuid, request: CreateEventRequest): EventDto
    fun update(eventId: Uuid, request: UpdateEventRequest): EventDto
    fun delete(eventId: Uuid)
    fun findById(eventId: Uuid): EventDto?
}
