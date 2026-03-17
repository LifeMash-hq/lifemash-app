package org.bmsk.lifemash.notification.data.source

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

internal interface FcmTokenFirestoreSource {
    suspend fun syncKeywords(fcmToken: String, keywords: List<String>)
    suspend fun updateUserId(fcmToken: String, userId: String)
}

internal class FcmTokenFirestoreSourceImpl : FcmTokenFirestoreSource {

    override suspend fun syncKeywords(fcmToken: String, keywords: List<String>) {
        tokenDocument(fcmToken).set(
            data = mapOf(
                Fields.KEYWORDS to keywords,
                Fields.UPDATED_AT to Clock.System.now().toEpochMilliseconds(),
            ),
            merge = true,
        )
    }

    override suspend fun updateUserId(fcmToken: String, userId: String) {
        tokenDocument(fcmToken).set(
            data = mapOf(Fields.USER_ID to userId),
            merge = true,
        )
    }

    private fun tokenDocument(fcmToken: String) =
        Firebase.firestore.collection(Fields.COLLECTION).document(fcmToken)

    private object Fields {
        const val COLLECTION = "fcm_tokens"
        const val KEYWORDS = "keywords"
        const val UPDATED_AT = "updatedAt"
        const val USER_ID = "userId"
    }
}
