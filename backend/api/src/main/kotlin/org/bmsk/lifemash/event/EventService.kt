package org.bmsk.lifemash.event

import org.bmsk.lifemash.model.calendar.CreateEventRequest
import org.bmsk.lifemash.model.calendar.EventDto
import org.bmsk.lifemash.model.calendar.UpdateEventRequest

interface EventService {
    fun getMonthEvents(groupId: String, userId: String, year: Int, month: Int): List<EventDto>
    fun create(groupId: String, userId: String, request: CreateEventRequest): EventDto
    fun update(groupId: String, userId: String, eventId: String, request: UpdateEventRequest): EventDto
    fun delete(groupId: String, userId: String, eventId: String)
}
