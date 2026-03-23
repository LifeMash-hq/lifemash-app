package org.bmsk.lifemash.calendar.domain.di

import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateEventUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.CreateGroupUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.DeleteEventUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMonthEventsUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCase
import org.bmsk.lifemash.calendar.domain.usecase.GetMyGroupsUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCase
import org.bmsk.lifemash.calendar.domain.usecase.JoinGroupUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateEventUseCaseImpl
import org.bmsk.lifemash.calendar.domain.usecase.UpdateGroupNameUseCase
import org.bmsk.lifemash.calendar.domain.usecase.UpdateGroupNameUseCaseImpl
import org.koin.dsl.module

val calendarDomainModule = module {
    factory<GetMonthEventsUseCase> { GetMonthEventsUseCaseImpl(get()) }
    factory<CreateEventUseCase> { CreateEventUseCaseImpl(get()) }
    factory<UpdateEventUseCase> { UpdateEventUseCaseImpl(get()) }
    factory<DeleteEventUseCase> { DeleteEventUseCaseImpl(get()) }
    factory<CreateGroupUseCase> { CreateGroupUseCaseImpl(get()) }
    factory<JoinGroupUseCase> { JoinGroupUseCaseImpl(get()) }
    factory<GetMyGroupsUseCase> { GetMyGroupsUseCaseImpl(get()) }
    factory<UpdateGroupNameUseCase> { UpdateGroupNameUseCaseImpl(get()) }
}
