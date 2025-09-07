package org.bmsk.lifemash.core.network.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONObject
import org.junit.rules.ExternalResource

class FirebaseInitRule(
    private val assetName: String = "google-services.json",
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
    }
}