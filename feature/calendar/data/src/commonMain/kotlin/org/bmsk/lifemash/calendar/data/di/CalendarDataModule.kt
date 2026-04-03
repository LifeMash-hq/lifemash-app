package org.bmsk.lifemash.calendar.data.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.calendar.data.api.CalendarApi
import org.bmsk.lifemash.calendar.data.api.FollowApi
import org.bmsk.lifemash.calendar.data.repository.CommentRepositoryImpl
import org.bmsk.lifemash.calendar.data.repository.EventRepositoryImpl
import org.bmsk.lifemash.calendar.data.repository.FollowRepositoryImpl
import org.bmsk.lifemash.calendar.data.repository.GroupRepositoryImpl
import org.bmsk.lifemash.calendar.domain.repository.CommentRepository
import org.bmsk.lifemash.calendar.domain.repository.EventRepository
import org.bmsk.lifemash.calendar.domain.repository.FollowRepository
import org.bmsk.lifemash.calendar.domain.repository.GroupRepository
import org.koin.dsl.module

val calendarDataModule = module {
    single { CalendarApi(get<HttpClient>()) }
    single { FollowApi(get<HttpClient>()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<FollowRepository> { FollowRepositoryImpl(get()) }
}
