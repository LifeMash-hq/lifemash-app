package org.bmsk.lifemash.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.bmsk.lifemash.group.GroupRepository
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import kotlin.uuid.Uuid

class FirebaseFcmService(private val groupRepository: GroupRepository) : FcmService {

    private val logger = LoggerFactory.getLogger(FirebaseFcmService::class.java)

    init {
        val saJson = org.bmsk.lifemash.config.EnvConfig.get("FIREBASE_SA_JSON")
        if (saJson != null && FirebaseApp.getApps().isEmpty()) {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(saJson.toByteArray())))
                .build()
            FirebaseApp.initializeApp(options)
        }
    }

    override fun notifyGroupExcept(groupId: Uuid, excludeUserId: Uuid, title: String, body: String) {
        if (FirebaseApp.getApps().isEmpty()) return

        val memberIds = groupRepository.getMemberUserIds(groupId).filter { it != excludeUserId }
        if (memberIds.isEmpty()) return

        val firestore = FirestoreClient.getFirestore()
        val tokens = mutableListOf<String>()

        for (userId in memberIds) {
            val docs = firestore.collection("fcm_tokens")
                .whereEqualTo("userId", userId.toString())
                .get().get().documents
            tokens.addAll(docs.map { it.id })
        }

        if (tokens.isEmpty()) return

        tokens.chunked(500).forEach { chunk ->
            val message = MulticastMessage.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .addAllTokens(chunk)
                .build()

            val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
            response.responses.forEachIndexed { idx, sendResponse ->
                if (!sendResponse.isSuccessful &&
                    sendResponse.exception?.messagingErrorCode?.name == "UNREGISTERED"
                ) {
                    firestore.collection("fcm_tokens").document(chunk[idx]).delete()
                }
            }

            logger.info("FCM sent to ${chunk.size} tokens, ${response.successCount} success")
        }
    }
}
