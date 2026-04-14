package org.bmsk.lifemash.data.remote.engine

import io.ktor.client.engine.HttpClientEngine

expect fun createPlatformHttpClientEngine(): HttpClientEngine
