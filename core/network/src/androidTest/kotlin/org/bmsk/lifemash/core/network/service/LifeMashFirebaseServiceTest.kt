package org.bmsk.lifemash.core.network.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.bmsk.lifemash.core.network.response.LifeMashArticleCategory
import org.json.JSONObject
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource

class LifeMashFirebaseServiceTest {

    @get:Rule
    val firebase = FirebaseInitRule(useEmulators = false)

    @Test
    fun `서비스테스트`(): Unit = runBlocking {
        val lifeMashFirebaseService = LifeMashFirebaseServiceImpl(
            FirebaseFirestore.getInstance()
        )

        lifeMashFirebaseService.getArticles(
            category = LifeMashArticleCategory.INTERNATIONAL,
        ).also {
            print(it)
        }
    }
}

class FirebaseInitRule(
    private val assetName: String = "google-services.json",
    private val useEmulators: Boolean = false
) : ExternalResource() {

    override fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 이미 초기화돼 있으면 스킵
        if (FirebaseApp.getApps(context).isNotEmpty()) return

        val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
        val root = JSONObject(json)

        val projectInfo = root.getJSONObject("project_info")
        val client0 = root.getJSONArray("client").getJSONObject(0)
        val clientInfo = client0.getJSONObject("client_info")
        val apiKey = client0.getJSONArray("api_key").getJSONObject(0).getString("current_key")

        val options = FirebaseOptions.Builder()
            .setProjectId(projectInfo.getString("project_id"))
            .setApplicationId(clientInfo.getString("mobilesdk_app_id"))
            .setApiKey(apiKey)
            .apply {
                // 선택 필드 처리
                if (projectInfo.has("storage_bucket")) {
                    setStorageBucket(projectInfo.getString("storage_bucket"))
                }
                if (projectInfo.has("project_number")) {
                    setGcmSenderId(projectInfo.getString("project_number"))
                }
            }
            .build()

        FirebaseApp.initializeApp(context, options)

//        if (useEmulators) {
//            // 필요할 때만 에뮬레이터로 전환 (포트는 사용 중인 서비스에 맞게)
//            try {
//                com.google.firebase.auth.FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
//            } catch (_: Throwable) { /* not using Auth */ }
//
//            try {
//                com.google.firebase.firestore.FirebaseFirestore.getInstance()
//                    .useEmulator("10.0.2.2", 8080)
//            } catch (_: Throwable) { /* not using Firestore */ }
//
//            try {
//                com.google.firebase.storage.FirebaseStorage.getInstance()
//                    .useEmulator("10.0.2.2", 9199)
//            } catch (_: Throwable) { /* not using Storage */ }
//        }
    }
}