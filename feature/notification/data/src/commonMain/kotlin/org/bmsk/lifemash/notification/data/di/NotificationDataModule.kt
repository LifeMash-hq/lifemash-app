package org.bmsk.lifemash.notification.data.di

import androidx.room.RoomDatabase
import org.bmsk.lifemash.notification.data.db.NotificationKeywordDB
import org.bmsk.lifemash.notification.data.repository.NotificationKeywordRepositoryImpl
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSource
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSourceImpl
import org.bmsk.lifemash.notification.domain.repository.KeywordRepository
import org.bmsk.lifemash.notification.domain.repository.KeywordSyncRepository
import org.koin.dsl.module

fun notificationDataModule(dbBuilder: RoomDatabase.Builder<NotificationKeywordDB>) = module {
    single<NotificationKeywordDB> { dbBuilder.build() }
    single { get<NotificationKeywordDB>().keywordDao() }
    single<FcmTokenFirestoreSource> { FcmTokenFirestoreSourceImpl() }
    single {
        NotificationKeywordRepositoryImpl(
            dao = get(),
            firestoreSource = get(),
        )
    }
    single<KeywordRepository> { get<NotificationKeywordRepositoryImpl>() }
    single<KeywordSyncRepository> { get<NotificationKeywordRepositoryImpl>() }
}
