package org.bmsk.lifemash.data.network.engine

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

actual fun createPlatformHttpClientEngine(): HttpClientEngine = OkHttp.create {
    config {
        readTimeout(90, TimeUnit.SECONDS)
    }
}
