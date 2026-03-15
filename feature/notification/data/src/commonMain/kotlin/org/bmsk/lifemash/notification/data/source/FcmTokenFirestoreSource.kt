package org.bmsk.lifemash.notification.data.source

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

/**
 * Firestore fcm_tokens/{token} 문서를 갱신한다.
 * keywords 필드: Cloud Functions이 키워드 알림 매칭에 사용
 * userId 필드:   Ktor 백엔드가 캘린더 알림 발송에 사용 (auth 모듈에서 세팅)
 */
internal class FcmTokenFirestoreSource {

    suspend fun syncKeywords(fcmToken: String, keywords: List<String>) {
        Firebase.firestore
            .collection("fcm_tokens")
            .document(fcmToken)
            .set(
                data = mapOf(
                    "keywords" to keywords,
                    "updatedAt" to kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
                ),
                merge = true,
            )
    }

    suspend fun updateUserId(fcmToken: String, userId: String) {
        Firebase.firestore
            .collection("fcm_tokens")
            .document(fcmToken)
            .set(
                data = mapOf("userId" to userId),
                merge = true,
            )
    }
}
