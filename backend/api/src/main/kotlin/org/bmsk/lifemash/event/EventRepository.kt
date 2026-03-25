package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest
import java.util.*

interface EventRepository {
    fun getMonthEvents(groupId: UUID, year: Int, month: Int): List<EventDto>
    fun create(groupId: UUID, authorId: UUID, request: CreateEventRequest): EventDto
    fun update(eventId: UUID, request: UpdateEventRequest): EventDto
    fun delete(eventId: UUID)
    fun findById(eventId: UUID): EventDto?
}
