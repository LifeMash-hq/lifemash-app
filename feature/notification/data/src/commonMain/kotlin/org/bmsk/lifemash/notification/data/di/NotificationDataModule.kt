package org.bmsk.lifemash.notification.data.di

import androidx.room.RoomDatabase
import org.bmsk.lifemash.notification.data.db.NotificationKeywordDB
import org.bmsk.lifemash.notification.data.repository.NotificationKeywordRepositoryImpl
import org.bmsk.lifemash.notification.data.source.FcmTokenFirestoreSource
import org.bmsk.lifemash.notification.domain.repository.NotificationKeywordRepository
import org.koin.dsl.module

/**
 * @param dbBuilder 플랫폼별 RoomDatabase.Builder를 주입
 * (Android: getNotificationKeywordDBBuilder(context),
 *  iOS: Phase 5에서 NSHomeDirectory 기반 경로 사용)
 */
fun notificationDataModule(dbBuilder: RoomDatabase.Builder<NotificationKeywordDB>) = module {
    single<NotificationKeywordDB> { dbBuilder.build() }
    single { get<NotificationKeywordDB>().keywordDao() }
    single<FcmTokenFirestoreSource> { FcmTokenFirestoreSource() }
    single<NotificationKeywordRepository> {
        NotificationKeywordRepositoryImpl(
            dao = get(),
            firestoreSource = get(),
        )
    }
}
