package org.bmsk.lifemash.calendar.domain.di

import org.bmsk.lifemash.calendar.domain.repository.CommentRepository
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCase
import org.koin.dsl.module

val calendarDomainModule = module {
    factory { GetMonthEventsUseCase(get<EventRepository>()) }
    factory { CreateEventUseCase(get<EventRepository>()) }
    factory { UpdateEventUseCase(get<EventRepository>()) }
    factory { DeleteEventUseCase(get<EventRepository>()) }
    factory { CreateGroupUseCase(get<GroupRepository>()) }
    factory { JoinGroupUseCase(get<GroupRepository>()) }
    factory { GetMyGroupsUseCase(get<GroupRepository>()) }
}
