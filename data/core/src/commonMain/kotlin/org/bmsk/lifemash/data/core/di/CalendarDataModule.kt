package org.bmsk.lifemash.data.core.di

import io.ktor.client.HttpClient
import org.bmsk.lifemash.domain.calendar.CommentRepository
import org.bmsk.lifemash.domain.calendar.EventRepository
import org.bmsk.lifemash.domain.calendar.FollowRepository
import org.bmsk.lifemash.domain.calendar.GroupRepository
import org.bmsk.lifemash.data.remote.calendar.CalendarApi
import org.bmsk.lifemash.data.remote.calendar.FollowApi
import org.bmsk.lifemash.data.core.calendar.CommentRepositoryImpl
import org.bmsk.lifemash.data.core.calendar.EventRepositoryImpl
import org.bmsk.lifemash.data.core.calendar.FollowRepositoryImpl
import org.bmsk.lifemash.data.core.calendar.GroupRepositoryImpl
import org.koin.dsl.module

val calendarDataModule = module {
    single { CalendarApi(get<HttpClient>()) }
    single { FollowApi(get<HttpClient>()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<FollowRepository> { FollowRepositoryImpl(get()) }
}
