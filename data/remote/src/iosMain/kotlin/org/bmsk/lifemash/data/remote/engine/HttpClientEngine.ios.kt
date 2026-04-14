package org.bmsk.lifemash.data.remote.engine

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpClientEngine(): HttpClientEngine = Darwin.create {
    configureRequest {
        setTimeoutInterval(90.0)
    }
}
